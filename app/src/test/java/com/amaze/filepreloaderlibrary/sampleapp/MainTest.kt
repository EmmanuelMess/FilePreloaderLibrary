package com.amaze.filepreloaderlibrary.sampleapp

import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.TextView
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith
import kotlinx.android.synthetic.main.activity_main.*
import org.awaitility.Awaitility.await
import org.junit.Assert
import org.junit.Before
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class SimpleTests {

    val testFolderName = "test"
    val testFileName = "test.txt"

    @Before
    fun setUp() {
        generateFiles()
    }

    private fun generateFiles() {
        val testFolder = File(Environment.getExternalStorageDirectory(), testFolderName)
        testFolder.mkdirs()
        val testFile = File(testFolder, testFileName)
        testFile.createNewFile()
    }

    @Test
    fun testOpenFolder() {
        val activity = Robolectric.setupActivity(MainActivity::class.java)
        doAndWaitForUpdate(activity) {
            shadowOf(activity.filelist).performItemClick(2)
        }
        val firstChild = activity.filelist.getChildAt(2)//TODO fix
        val firstChildText = firstChild.text1.text

        Assert.assertEquals(testFileName, firstChildText)
    }

    private fun doAndWaitForUpdate(activity: MainActivity, action: () -> Unit) {
        val hash = contentAwareHash(activity)
        action()
        await().pollInterval(50, TimeUnit.MILLISECONDS).until{
            hash != contentAwareHash(activity)
        }
    }

    private fun contentAwareHash(activity: MainActivity): Int {
        return activity.timeView.text.toString().hashCode()
    }

    private val View.text1 get() = findViewById<TextView>(android.R.id.text1)
}