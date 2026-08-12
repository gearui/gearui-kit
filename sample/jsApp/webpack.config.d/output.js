// 宿主产物必须关掉 UMD wrapper。
//
// kotlin-webpack 默认生成的 UMD 尾部会 `for (var i in a) root[i] = a[i]`，
// 把宿主 bundle 的顶层导出整体挂到 window。宿主里含 @JsExport 的
// core-render-web 文件会产生一个 `com` 键，于是 window.com 被整体替换，
// 抹掉业务 bundle 先挂上去的 com.tencent.kuikly.core.nvi.registerCallNative，
// 表现为 "registerCallNative error" + "callNative is not defined"。
//
// 宿主是可执行入口、不需要对外导出任何符号，所以直接走 iife。
config.output = config.output || {};
config.output.libraryTarget = undefined;
config.output.library = undefined;
config.output.iife = true;
