"""Orchestrator component for controlling a Faucet SDN"""

import http.server
import logging
import socketserver
import sys
import threading

import faucet_event_client
import configurator

logging.basicConfig(level=logging.INFO)
LOGGER = logging.getLogger('forch')


class RequestHandler(http.server.BaseHTTPRequestHandler):

    def do_GET(self):
        self.send_response(200)
        self.end_headers()
        message = threading.currentThread().getName() + '\n'
        self.wfile.write(message.encode())


class ThreadedHTTPServer(socketserver.ThreadingMixIn, http.server.HTTPServer):
    """Handle requests in a separate thread."""


class Forchestrator:
    """Main class encompassing faucet orchestrator components for dynamically
    controlling faucet ACLs at runtime"""

    def __init__(self, config):
        self._config = config
        self._faucet_events = None
        self._server = None

    def initialize(self):
        """Initialize forchestrator instance"""
        LOGGER.info('Attaching event channel...')
        self._faucet_events = faucet_event_client.FaucetEventClient(self._config)
        self._faucet_events.connect()

    def main_loop(self):
        """Main event processing loop"""
        LOGGER.info('Entering main event loop...')
        while self._handle_faucet_events():
            pass

    def _handle_faucet_events(self):
        while self._faucet_events:
            event = self._faucet_events.next_event()
            LOGGER.debug('Faucet event %s', event)
            if not event:
                return True
            (dpid, port, active) = self._faucet_events.as_port_state(event)
            if dpid and port:
                LOGGER.info('Port state %s %s %s', dpid, port, active)
            (dpid, port, target_mac) = self._faucet_events.as_port_learn(event)
            if dpid and port:
                LOGGER.info('Port learn %s %s %s', dpid, port, target_mac)
            (dpid, restart_type) = self._faucet_events.as_config_change(event)
            if dpid is not None:
                LOGGER.info('DP restart %d %s', dpid, restart_type)
        return False

    def start_server(self):
        """Start serving thread"""
        address=('0.0.0.0', 9019)
        LOGGER.info('Starting http server on %s', address)
        self._server = ThreadedHTTPServer(address, RequestHandler)

        thread = threading.Thread(target = self._server.serve_forever)
        thread.deamon = False
        thread.start()



if __name__ == '__main__':
    CONFIG = configurator.Configurator()
    FORCH = Forchestrator(CONFIG.parse_args(sys.argv))
    FORCH.initialize()
    FORCH.start_server()
    FORCH.main_loop()
    LOGGER.info('Forchestrating...')
