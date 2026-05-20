// Bundle every Kotlin/JS module into a single GearUISample.js so plain
// browser <script> loading works. Without this, dev mode emits multiple
// chunks linked through UMD `define([...], factory)` — which has no AMD
// loader in the browser and falls back to global lookups that fail.
config.optimization = config.optimization || {};
config.optimization.splitChunks = false;
config.optimization.runtimeChunk = false;
