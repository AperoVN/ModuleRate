# ModuleRate
## How to use
```
  implementation 'apero:module.rate:1.0.7'
```

Application:
```
  ModuleRate.install(this, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME,getString(R.string.appname))
  
  ModuleRate.setOnDismissAppOpenListener {
      AppOpenManager.getInstance().disableAdResumeByClickAction()
  }
```
Fetch remote config:
```
  ModuleRate.syncWithRemoteConfig(remoteConfig)
```
Change Color App
```
  <color name="clr_rate_primary">@color/clr_primary</color>
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
