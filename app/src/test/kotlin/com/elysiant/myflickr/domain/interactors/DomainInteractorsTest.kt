package com.elysiant.myflickr.domain.interactors

import com.elysiant.myflickr.MyFlickrTestBase
import com.elysiant.myflickr.common.MyFlickrConstants
import com.elysiant.myflickr.domain.interactors.mocks.MockMyFlickrStartupDataInteractor
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DomainInteractorsTest : MyFlickrTestBase() {

    @Test
    fun testStartupDataInteractor() {

        val photosList = flickrApi.searchPhotos("testing", 1).blockingFirst().response()?.body()?.photos?.photosList

        Mockito.`when` (mockLruCache.get(MyFlickrConstants.CACHE_KEY_START_UP_PHOTOS))
                .thenReturn(photosList)

        // Test cache population from local resources file

        val cachedResult = mockLruCache.get(MyFlickrConstants.CACHE_KEY_START_UP_PHOTOS) as? List<*>
        Assert.assertNotNull(cachedResult)
        Assert.assertEquals(50, cachedResult?.size)
    }

    @Test
    fun testSearchPhotosUsingPhotosDataInteractor() {

        val observable = photosDataInteractor.searchForPhotos("dogs", 1)
        val searchResponse = observable.blockingFirst().response()?.body()
        Assertions.assertThat(searchResponse).isNotNull()
        Assertions.assertThat(searchResponse?.photos?.photosList?.size).isEqualTo(50)

        // Test stats
        val photos = searchResponse?.photos
        Assertions.assertThat(photos).isNotNull()
        Assertions.assertThat(photos?.pageNo).isEqualTo(250)
        Assertions.assertThat(photos?.totalNoOfPages).isEqualTo(2423)
        Assertions.assertThat(photos?.perPageCount).isEqualTo(50)

        // Test first photo
        val photosList = searchResponse?.photos?.photosList
        Assertions.assertThat(photosList?.size).isEqualTo(50)
        val firstPhoto = photosList?.get(0)
        Assertions.assertThat(firstPhoto).isNotNull()

        Assertions.assertThat(firstPhoto?.id).isEqualTo("50496130892")
        Assertions.assertThat(firstPhoto?.owner).isEqualTo("29954948@N06")
        Assertions.assertThat(firstPhoto?.title).isEqualTo("Luna & Zuice in the light")
        Assertions.assertThat(firstPhoto?.smallUrl).isEqualTo("https://live.staticflickr.com/65535/50496130892_9573bd201f_m.jpg")
    }

}