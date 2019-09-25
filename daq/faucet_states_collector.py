"""Processing faucet events"""
import json
from datetime import datetime


def dump_states(func):
    """Decorator to dump the current states after the states map is modified"""
    def set_default(obj):
        if isinstance(obj, set):
            return list(obj)
        return obj

    def wrapped(self, *args, **kwargs):
        res = func(self, *args, **kwargs)
        print(json.dumps(self.system_states, default=set_default))
        return res

    return wrapped


class FaucetStatesCollector:
    """Processing faucet events and store states in the map"""

    MAP_ENTRY_SWITCH = "dpids"
    MAP_ENTRY_PORTS = "ports"
    MAP_ENTRY_PORT_STATE_CHANGED_COUNT = "change_count"
    MAP_ENTRY_PORT_STATUS = "is_up"
    MAP_ENTRY_PORT_LAST_CHANGED_TS = "last_changed_timestamp"
    MAP_ENTRY_LEARNED_MACS = "learned_macs"
    MAP_ENTRY_MAC_LEARNING_PORT = "port"
    MAP_ENTRY_MAC_LEARNING_IP = "ip_address"
    MAP_ENTRY_MAC_LEARNING_TS = "timestamp"
    MAP_ENTRY_CONFIG_CHANGE_COUNT = "change_count"
    MAP_ENTRY_LAST_RESTART_TYPE = "last_restart"
    MAP_ENTRY_LAST_RESTART_TS = "last_restart_timestamp"

    def __init__(self):
        self.system_states = {FaucetStatesCollector.MAP_ENTRY_SWITCH: {}}
        self.switch_states = self.system_states[FaucetStatesCollector.MAP_ENTRY_SWITCH]

    def get_switches(self, switch_name):
        """get the system states"""
        switch_map = {}

        # filling switch attributes
        attributes_map = switch_map.setdefault("attributes", {})
        attributes_map["name"] = ""
        attributes_map["dp_id"] = switch_name
        attributes_map["description"] = ""

        # filling switch dynamics
        switch_states = self.switch_states.get(int(switch_name), {})
        switch_map["config_change_count"] = \
            switch_states.get(FaucetStatesCollector.MAP_ENTRY_CONFIG_CHANGE_COUNT, "")
        switch_map["last_restart_type"] = \
            switch_states.get(FaucetStatesCollector.MAP_ENTRY_LAST_RESTART_TYPE, "")
        switch_map["last_restart_timestamp"] = \
            switch_states.get(FaucetStatesCollector.MAP_ENTRY_LAST_RESTART_TS, "")

        switch_port_map = switch_map.setdefault("ports", {})

        # filling port information
        for port_id, port_states in switch_states\
            .get(FaucetStatesCollector.MAP_ENTRY_PORTS, {}).items():
            port_map = switch_port_map.setdefault(port_id, {})
            # port attributes
            switch_port_attributes_map = port_map.setdefault("attributes", {})
            switch_port_attributes_map["name"] = ""
            switch_port_attributes_map["description"] = ""
            switch_port_attributes_map["stack_peer_switch"] = ""
            switch_port_attributes_map["stack_peer_port"] = ""

            # port dynamics
            port_map["is_up"] = \
                port_states.get(FaucetStatesCollector.MAP_ENTRY_PORT_STATUS, "")
            port_map["port_type"] = ""
            port_map["last_changed_timestamp"] = \
                port_states.get(FaucetStatesCollector.MAP_ENTRY_PORT_LAST_CHANGED_TS, "")
            port_map["change_count"] = \
                port_states.get(FaucetStatesCollector.MAP_ENTRY_PORT_STATE_CHANGED_COUNT, "")
            port_map["packet_count"] = ""

        # filling learned macs
        switch_learned_mac_map = switch_map.setdefault("learned_macs", {})
        system_learned_mac_states = \
            self.system_states.get(FaucetStatesCollector.MAP_ENTRY_LEARNED_MACS, {})
        for mac in switch_states.get(FaucetStatesCollector.MAP_ENTRY_LEARNED_MACS, set()):
            mac_map = switch_learned_mac_map.setdefault(mac, {})
            mac_states = system_learned_mac_states.get(mac, {})
            mac_map["port"] =\
                mac_states.get(FaucetStatesCollector.MAP_ENTRY_MAC_LEARNING_PORT, "")
            mac_map["timestamp"] = \
                mac_states.get(FaucetStatesCollector.MAP_ENTRY_MAC_LEARNING_TS, "")
            mac_map["ip_address"] = \
                mac_states.get(FaucetStatesCollector.MAP_ENTRY_MAC_LEARNING_IP, "")

        return switch_map

    @dump_states
    def process_port_state(self, dpid, port, status, timestamp):
        """process port state event"""
        port_table = self.switch_states\
            .setdefault(dpid, {})\
            .setdefault(FaucetStatesCollector.MAP_ENTRY_PORTS, {})\
            .setdefault(port, {})

        port_table[FaucetStatesCollector.MAP_ENTRY_PORT_STATUS] = status
        port_table[FaucetStatesCollector.MAP_ENTRY_PORT_LAST_CHANGED_TS] = \
            datetime.fromtimestamp(timestamp).isoformat()

        port_table[FaucetStatesCollector.MAP_ENTRY_PORT_STATE_CHANGED_COUNT] = \
            port_table.setdefault(
                FaucetStatesCollector.MAP_ENTRY_PORT_STATE_CHANGED_COUNT, 0) + 1

    @dump_states
    # pylint: disable=too-many-arguments
    def process_port_learn(self, dpid, port, mac, src_ip, timestamp):
        """process port learn event"""
        # update global mac table
        global_mac_table = self.system_states\
            .setdefault(FaucetStatesCollector.MAP_ENTRY_LEARNED_MACS, {})\
            .setdefault(mac, {})

        global_mac_table[FaucetStatesCollector.MAP_ENTRY_MAC_LEARNING_IP] = src_ip
        global_mac_table[FaucetStatesCollector.MAP_ENTRY_MAC_LEARNING_PORT] = port
        global_mac_table[FaucetStatesCollector.MAP_ENTRY_MAC_LEARNING_TS] = \
            datetime.fromtimestamp(timestamp).isoformat()

        # update per switch mac table
        self.switch_states\
            .setdefault(dpid, {})\
            .setdefault(FaucetStatesCollector.MAP_ENTRY_LEARNED_MACS, set())\
            .add(mac)

    @dump_states
    def process_config_change(self, dpid, restart_type, timestamp):
        """process config change event"""
        config_change_table = self.switch_states\
            .setdefault(dpid, {})

        config_change_table[FaucetStatesCollector.MAP_ENTRY_LAST_RESTART_TYPE] = restart_type
        config_change_table[FaucetStatesCollector.MAP_ENTRY_LAST_RESTART_TS] = \
            datetime.fromtimestamp(timestamp).isoformat()
        config_change_table[FaucetStatesCollector.MAP_ENTRY_CONFIG_CHANGE_COUNT] = \
            config_change_table.setdefault(
                FaucetStatesCollector.MAP_ENTRY_CONFIG_CHANGE_COUNT, 0) + 1
