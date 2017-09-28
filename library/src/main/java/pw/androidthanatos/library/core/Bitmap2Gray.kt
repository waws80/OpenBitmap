package pw.androidthanatos.library.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.*

/**
 * Bitmap 灰化  素描
 */
class Bitmap2Gray {

    private val srcMat: Mat by lazy { Mat() }

    private val resultMat: Mat by lazy { Mat() }

    /**
     * 灰化bitmap
     */
    fun toGray(bitmap: Bitmap){
        Utils.bitmapToMat(bitmap,srcMat)
        Imgproc.cvtColor(srcMat,resultMat,Imgproc.COLOR_BGR2GRAY)
        Utils.matToBitmap(resultMat,bitmap)
    }

    fun toGray(path: String): Bitmap{
        val bitmap = BitmapFactory.decodeFile(path)
        Utils.bitmapToMat(bitmap,srcMat)
        Imgproc.cvtColor(srcMat,resultMat,Imgproc.COLOR_BGR2GRAY)
        Utils.matToBitmap(resultMat,bitmap)
        return bitmap
    }


    fun toThreshold(bitmap: Bitmap){
        Utils.bitmapToMat(bitmap,srcMat)
        Imgproc.cvtColor(srcMat,resultMat,Imgproc.COLOR_BGR2GRAY)
        Imgproc.adaptiveThreshold(resultMat,resultMat,255.0,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY,3,0.0)
        Utils.matToBitmap(resultMat,bitmap)
    }

}