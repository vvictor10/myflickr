package com.elysiant.myflickr.ui.component

import com.elysiant.myflickr.app.injection.component.MyFlickrComponent
import com.elysiant.myflickr.ui.photos.FullscreenPhotoActivity
import com.elysiant.myflickr.ui.photos.SearchActivity
import com.elysiant.myflickr.ui.scope.ActivityScope
import dagger.Component

@ActivityScope
@Component(dependencies = [MyFlickrComponent::class])
interface ActivityComponent {

    fun inject(activity: SearchActivity)
    fun inject(activity: FullscreenPhotoActivity)

}
