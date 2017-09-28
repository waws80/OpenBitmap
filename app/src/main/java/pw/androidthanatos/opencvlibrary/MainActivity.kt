package pw.androidthanatos.opencvlibrary

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast.*
import pw.androidthanatos.library.OpenBitmap

class MainActivity : AppCompatActivity() {

    private val PIC_REQUEST_CODE = 100

    private lateinit var mImageView: ImageView

    private var mHandler = Handler(Looper.getMainLooper())

    private lateinit var mOpenBitmap: OpenBitmap

    private lateinit var mDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDialog = AlertDialog.Builder(this).create()
        mDialog.setView(ProgressBar(this))
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.setCancelable(false)
        mOpenBitmap = OpenBitmap.install(this)
        mImageView = findViewById(R.id.iv) as ImageView
    }

    /**
     * 打开图库选择照片
     */
    fun chosePicture(v: View){
        val intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent,PIC_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_REQUEST_CODE  && resultCode == Activity.RESULT_OK){
            if (null != data){
                Thread{
                    val uri = data.data
                    val columns = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = contentResolver.query(uri,columns,null,null,null)
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(columns[0])
                    val path = cursor.getString(columnIndex)
                    val bitmap = BitmapFactory.decodeFile(path)
                    log("${bitmap.byteCount}")
                    cursor.close()
                    doSomething(path,bitmap)
                }.start()
            }else{
                dialog("获取图片失败！")
            }

        }
    }

    /**
     * 处理图片
     * @param path 图片的路径
     * @param bitmap Bitmap
     */
    private fun doSomething(path: String, bitmap: Bitmap) {
        mHandler.post {
            mImageView.setImageBitmap(bitmap)
            mDialog.show()
        }
        val rects = mOpenBitmap.faceRecogniticon().scanFace(bitmap,true)
        if (rects != null){
            mHandler.post {
                dialog("人脸个数：${rects.size()}")
            }
            log("bitmap info:\n width: ${bitmap.width}")
            rects.toArray().forEach { log(" X:${it.x}   Y:${it.y} \n width:${it.width}   height:${it.height}") }
        }else{
            log("OpenBitmap初始化失败！")
        }
        
        mHandler.post {
            mImageView.setImageBitmap(bitmap)
            mDialog.dismiss()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        OpenBitmap.Uninstall()
    }


    private fun dialog(msg: String = "", duration: Int = LENGTH_SHORT){
        makeText(this,msg, duration).show()
    }

    private fun log(msg: String = ""){
        Log.d("OpenCVLibrary",msg)
    }
}
