#import "KuiklyRenderViewController.h"
#import <OpenKuiklyIOSRender/KuiklyRenderViewControllerBaseDelegator.h>
#import <OpenKuiklyIOSRender/KuiklyRenderContextProtocol.h>
#import <OpenKuiklyIOSRender/KuiklyRenderThreadManager.h>
#import <OpenKuiklyIOSRender/KuiklyRenderBridge.h>
#import <SDWebImage/UIImageView+WebCache.h>

@interface KuiklyRenderComponentExpandHandler : NSObject<KuiklyRenderComponentExpandProtocol>
@end

@implementation KuiklyRenderComponentExpandHandler

+ (NSString *)resolveAssetPath:(NSString *)relativePath {
    NSString *name = [[relativePath lastPathComponent] stringByDeletingPathExtension];
    NSString *ext = [relativePath pathExtension];
    NSString *dir = [relativePath stringByDeletingLastPathComponent];
    NSString *composeDir = [@"compose-resources/" stringByAppendingString:dir];

    NSArray<NSBundle *> *bundles = @[
        [NSBundle mainBundle],
        [NSBundle bundleForClass:[self class]]
    ];

    for (NSBundle *bundle in bundles) {
        NSString *resolved = [bundle pathForResource:name ofType:ext inDirectory:dir];
        if (resolved.length > 0) return resolved;
        resolved = [bundle pathForResource:name ofType:ext inDirectory:composeDir];
        if (resolved.length > 0) return resolved;
    }

    for (NSBundle *bundle in [NSBundle allBundles]) {
        NSString *resolved = [bundle pathForResource:name ofType:ext inDirectory:dir];
        if (resolved.length > 0) return resolved;
        resolved = [bundle pathForResource:name ofType:ext inDirectory:composeDir];
        if (resolved.length > 0) return resolved;
    }

    for (NSBundle *bundle in [NSBundle allFrameworks]) {
        NSString *resolved = [bundle pathForResource:name ofType:ext inDirectory:dir];
        if (resolved.length > 0) return resolved;
        resolved = [bundle pathForResource:name ofType:ext inDirectory:composeDir];
        if (resolved.length > 0) return resolved;
    }

    return nil;
}

+ (void)load {
    [KuiklyRenderBridge registerComponentExpandHandler:[self new]];
}

- (BOOL)hr_setImageWithUrl:(NSString *)url forImageView:(UIImageView *)imageView {
    if (url.length == 0) {
        imageView.image = nil;
        return YES;
    }

    if ([url hasPrefix:@"assets://"]) {
        NSString *path = [url stringByReplacingOccurrencesOfString:@"assets://" withString:@""];
        NSString *resolvedPath = [KuiklyRenderComponentExpandHandler resolveAssetPath:path];
        imageView.image = resolvedPath.length > 0 ? [UIImage imageWithContentsOfFile:resolvedPath] : nil;
        return YES;
    }

    if ([url hasPrefix:@"file://"]) {
        NSString *path = [[NSURL URLWithString:url] path];
        imageView.image = [UIImage imageWithContentsOfFile:path];
        return YES;
    }

    [imageView sd_setImageWithURL:[NSURL URLWithString:url]];
    return YES;
}

- (UIColor *)hr_colorWithValue:(NSString *)value {
    return nil;
}

@end

@interface KuiklyRenderViewController()<KuiklyRenderViewControllerBaseDelegatorDelegate>

@property (nonatomic, strong) KuiklyRenderViewControllerBaseDelegator *delegator;
// Phase 0 spike：iOS 没有自动 BACK 桥，浮一个按钮主动触发
// [delegator onBackPressedWithCompletion:]，回填 consumed 真值到 label。
// 仅用于 sample 验证 Kuikly BackHandler 链路，不是 GearUI runtime API。
@property (nonatomic, strong) UIButton *kuiklyBackProbeButton;
@property (nonatomic, strong) UILabel *kuiklyBackProbeLabel;
@property (nonatomic, assign) NSInteger kuiklyBackProbeCounter;

@end

@implementation KuiklyRenderViewController {
    NSDictionary *_pageData;
}

- (instancetype)initWithPageName:(NSString *)pageName pageData:(NSDictionary *)pageData {
    if (self = [super init]) {
        _pageData = pageData ?: @{};
        _delegator = [[KuiklyRenderViewControllerBaseDelegator alloc] initWithPageName:pageName pageData:_pageData];
        _delegator.delegate = self;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];

    // delegator 会自动创建和管理 render view
    [_delegator viewDidLoadWithView:self.view];

    [self installKuiklyBackProbeOverlay];
}

#pragma mark - Phase 0 BACK probe

- (void)installKuiklyBackProbeOverlay {
    if (self.kuiklyBackProbeButton != nil) {
        return;
    }

    UIButton *button = [UIButton buttonWithType:UIButtonTypeSystem];
    button.translatesAutoresizingMaskIntoConstraints = NO;
    [button setTitle:@"Simulate iOS BACK" forState:UIControlStateNormal];
    button.titleLabel.font = [UIFont systemFontOfSize:14 weight:UIFontWeightSemibold];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    button.backgroundColor = [UIColor colorWithRed:0.07 green:0.41 blue:0.86 alpha:0.92];
    button.layer.cornerRadius = 20;
    button.contentEdgeInsets = UIEdgeInsetsMake(8, 16, 8, 16);
    [button addTarget:self action:@selector(handleKuiklyBackProbeTap:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:button];
    self.kuiklyBackProbeButton = button;

    UILabel *label = [[UILabel alloc] init];
    label.translatesAutoresizingMaskIntoConstraints = NO;
    label.font = [UIFont systemFontOfSize:12];
    label.textColor = [UIColor whiteColor];
    label.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.65];
    label.textAlignment = NSTextAlignmentCenter;
    label.numberOfLines = 2;
    label.layer.cornerRadius = 8;
    label.clipsToBounds = YES;
    label.text = @"BACK probe ready\nlast result: -";
    [self.view addSubview:label];
    self.kuiklyBackProbeLabel = label;

    UILayoutGuide *safe = self.view.safeAreaLayoutGuide;
    [NSLayoutConstraint activateConstraints:@[
        [button.trailingAnchor constraintEqualToAnchor:safe.trailingAnchor constant:-16],
        [button.bottomAnchor constraintEqualToAnchor:safe.bottomAnchor constant:-16],

        [label.leadingAnchor constraintEqualToAnchor:safe.leadingAnchor constant:16],
        [label.trailingAnchor constraintEqualToAnchor:button.leadingAnchor constant:-12],
        [label.centerYAnchor constraintEqualToAnchor:button.centerYAnchor],
        [label.heightAnchor constraintGreaterThanOrEqualToConstant:36],
    ]];
}

- (void)handleKuiklyBackProbeTap:(UIButton *)sender {
    self.kuiklyBackProbeCounter += 1;
    NSInteger callId = self.kuiklyBackProbeCounter;
    __weak typeof(self) weakSelf = self;
    [_delegator onBackPressedWithCompletion:^(BOOL consumed) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (strongSelf == nil) return;
        NSString *result = consumed ? @"consumed=YES" : @"consumed=NO";
        NSLog(@"[BackProbe #%ld] %@", (long)callId, result);
        strongSelf.kuiklyBackProbeLabel.text =
            [NSString stringWithFormat:@"BACK #%ld\n%@", (long)callId, result];
    }];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];

    // 调试日志：检查安全区域和 view 尺寸
    NSLog(@"[PrivUISample] viewDidLayoutSubviews called");
    NSLog(@"[PrivUISample] view.bounds: %@", NSStringFromCGRect(self.view.bounds));
    NSLog(@"[PrivUISample] safeAreaInsets: %@", NSStringFromUIEdgeInsets(self.view.safeAreaInsets));

    // 关键：在布局完成后通知 delegator，此时 safeAreaInsets 已经正确设置
    // 这样 KuiklyUI Runtime 就能获取到正确的可用渲染区域（已排除状态栏/刘海）
    [_delegator viewDidLayoutSubviews];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [_delegator viewWillAppear];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [_delegator viewDidAppear];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [_delegator viewWillDisappear];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [_delegator viewDidDisappear];
}

+ (void)performOnKuiklyContextQueue:(dispatch_block_t)block {
    // 在 KuiklyUI 的 context 队列上执行任务
    [KuiklyRenderThreadManager performOnContextQueueWithBlock:block];
}

#pragma mark - KuiklyRenderViewControllerBaseDelegatorDelegate

- (UIView *)delegatorRootView {
    return self.view;
}

- (UIViewController *)delegatorViewController {
    return self;
}

- (void)fetchContextCodeWithPageName:(NSString *)pageName resultCallback:(void (^)(NSString * _Nullable, NSError * _Nullable))callback {
    // 返回 framework 名称，对应 build.gradle.kts 中的 baseName = "GearUISample"
    callback(@"GearUISample", nil);
}

@end
