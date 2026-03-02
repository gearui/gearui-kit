#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface KuiklyRenderViewController : UIViewController

/**
 * @brief 创建实例对应的初始化方法
 * @param pageName 页面名（对应 kotlin 侧页面注解 @Page("xxxx") 中的 xxx 名）
 * @param pageData 页面对应的参数（kotlin 侧可通过 pageData.params 获取）
 * @return 返回 KuiklyRenderViewController 实例
 */
- (instancetype)initWithPageName:(NSString *)pageName pageData:(NSDictionary *)pageData;

/**
 * 在 KuiklyUI 的 context 线程上执行任务
 * 
 * @param block 要执行的任务
 */
+ (void)performOnKuiklyContextQueue:(dispatch_block_t)block;

@end

NS_ASSUME_NONNULL_END
