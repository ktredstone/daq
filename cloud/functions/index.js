const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);
var db = admin.firestore();

exports.daq_firestore = functions.pubsub.topic('daq_runner').onPublish((event) => {
  const message = event.json;
  const origin = event.attributes.origin;
  const port = 'port-' + message.port;
  const test = message.name;
  const timestamp = String(Date.now());
  const timestr = new Date().toTimeString();
  message.updated = timestr;
  port_doc = db
    .collection('origin').doc(origin)
    .collection('port').doc(port);
  port_doc
    .set({'updated': timestr});
  port_doc
    .collection('test').doc(test)
    .collection('time').doc(timestamp)
    .set(message);
  return null;
});