"""Processing faucet events"""
import json
from datetime import datetime


def save_states(func):
    """Decorator to dump the current states after the states map is modified"""
    def wrapped(self, *args, **kwargs):
        res = func(self, *args, **kwargs)
        print(json.dumps(self.system_states))
        return res

    return wrapped


class FaucetStatesCollector:
    """Processing faucet events and store states in the map"""

    MAP_ENTRY_SWITCH = "dpids"
    MAP_ENTRY_PORTS = "ports"
    MAP_ENTRY_PORT_STATE_CHANGED_COUNT = "change_count"
    MAP_ENTRY_PORT_STATUS = "status"
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

    def get_switches(self):
        """get the system states"""
        return self.system_states

    @save_states
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

    @save_states
    def process_port_learn(self, dpid, port, mac, src_ip, timestamp):
        """process port learn event"""
        mac_table = self.switch_states\
            .setdefault(dpid, {})\
            .setdefault(FaucetStatesCollector.MAP_ENTRY_LEARNED_MACS, {})\
            .setdefault(mac, {})

        mac_table[FaucetStatesCollector.MAP_ENTRY_MAC_LEARNING_IP] = src_ip
        mac_table[FaucetStatesCollector.MAP_ENTRY_MAC_LEARNING_PORT] = port
        mac_table[FaucetStatesCollector.MAP_ENTRY_MAC_LEARNING_TS] = \
            datetime.fromtimestamp(timestamp).isoformat()

    @save_states
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
