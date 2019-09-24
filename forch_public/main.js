
const viewer_data = {}

function load_viewer() {
  const container = document.getElementById('viewer_container')
  const options = {
    mode: 'view'
  };
  const editor = new JSONEditor(container, options);
  return editor;
}

function update_editor(editor, category, json) {
  console.log('Updating', category, 'with', json);
  viewer_data[category] = json;
  editor.set(viewer_data);
}

function fetch_data(editor, category, data_url) {
  console.log('Fetching', data_url)
  fetch(data_url)
    .then(response => response.json())
    .then(json => update_editor(editor, category, json))
    .catch(rejection => console.log(rejection));
}

function initialize() {
  console.log('initializing viewer');
  const viewer = load_viewer();
  fetch_data(viewer, 'overview', 'overview');
  fetch_data(viewer, 'topology', 'topology');
  fetch_data(viewer, 'switches', 'switches');

  switches = {
    'hello': {
      'id': 10000
    }
  };
  switch_name = 'hello';
  const temp = document.getElementById('switch_row_template');
  const table_row_html = temp.innerHTML;
  const table_row = eval('`' + table_row_html + '`');
  const row_holder = document.createElement('div');
  row_holder.innerHTML = table_row;
  const table_element = document.querySelector('#switch_table table');
  const row_element = row_holder.firstElementChild;
  table_element.appendChild(row_element);
}
