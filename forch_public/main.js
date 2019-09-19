
function load_viewer() {
  const container = document.getElementById('viewer_container')
  const options = {
    mode: 'view'
  };
  const editor = new JSONEditor(container, options);
  return editor;
}

function initialize() {
  console.log('initializing viewer');
  const viewer = load_viewer();
  viewer.set({'hello': True});
}
