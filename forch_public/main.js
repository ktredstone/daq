
const data = {}
let editor = null;

function load_viewer() {
  const container = document.getElementById('viewer_container')
  const options = {
    mode: 'view'
  };
  editor = new JSONEditor(container, options);
}

function data_update(category, json, func) {
  console.log('Updating', category, 'with', json);
  data[category] = json;
  editor.set(data);
  func && func();
}

function fetch_data(category, data_url, func) {
  console.log('Fetching', data_url)
  fetch(data_url)
    .then(response => response.json())
    .then(json => data_update(category, json, func))
    .catch(rejection => console.log(rejection));
}

function populate_table() {
  console.log('Populating switches', data.switches);
  if (!data.switches.names) {
    document.getElementById('switch_table').innerHTML = 'No switches to be found!';
    return;
  }
  
  const switches = data.switches.names.keys();
  console.log('switches', switches)

  const temp = document.getElementById('switch_row_template');
  const table_row_html = temp.innerHTML;
  const table_row = eval('`' + table_row_html + '`');
  const row_holder = document.createElement('div');
  row_holder.innerHTML = table_row;
  const table_element = document.querySelector('#switch_table table');
  const row_element = row_holder.firstElementChild;
  table_element.appendChild(row_element);
}

function initialize() {
  console.log('initializing viewer');
  load_viewer();
  fetch_data('overview', 'overview');
  fetch_data('topology', 'topology');
  fetch_data('switches', 'switches', populate_table);
}
