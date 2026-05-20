// Force devServer.port — KMP doesn't expose port through commonWebpackConfig
// directly in 2.1.21, but webpack.config.d entries are merged into the final
// webpack config so this overrides whatever default the wrapper picks.
;(function() {
  if (config.devServer === undefined || config.devServer === null) {
    config.devServer = {};
  }
  config.devServer.port = 8083;
  config.devServer.open = false;
  config.devServer.headers = config.devServer.headers || {};
  config.devServer.headers["Access-Control-Allow-Origin"] = "*";
})();
