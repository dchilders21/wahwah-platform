if (window.location.hash == "#_")
  window.location.hash = "#";


// Provide top-level namespaces for our javascript.
(function() {
  window.platform = {};

  // Set the class namespaces
  platform.forms = {};
  platform.routers = {};
  platform.models = {};
  platform.views = {};

  // Set instance namespaces where
  // instances of the classes will live
  platform.app = {};
  platform.app.routers = {};
  platform.app.ui = {};
  platform.app.data = {};

})();

