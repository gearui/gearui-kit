// host dev server on 8081; the business bundle (sample) runs on 8083.
;(function() {
  if (config.devServer === undefined || config.devServer === null) {
    config.devServer = {};
  }
  config.devServer.port = 8081;
  config.devServer.open = false;
})();
