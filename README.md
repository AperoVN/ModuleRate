# ModuleRate
## How to use
```
  implementation 'apero:module.rate:1.0.1'
```

Application:
```
  ModuleRate.install(this, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME)
  
  ModuleRate.setOnDismissAppOpenListener {
      AppOpenManager.getInstance().disableAdResumeByClickAction()
  }
```
Fetch remote config:
```
  ModuleRate.syncWithRemoteConfig(remoteConfig)
```
Show Rate App:
```
  ModuleRate.showRate(supportFragmentManager) {
    //TODO invoke rated
  }
```
Show Feedback:
```
  ModuleRate.showFeedback(context)
```
