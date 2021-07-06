package com.dsige.dominion.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.dsige.dominion.R
import com.dsige.dominion.data.local.model.OtDetalle
import com.dsige.dominion.data.local.model.OtPhoto
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.*

object Util {

    const val UrlFoto = "http://190.223.38.245/WebApi_3R_Dominion/Archivos/Fotos/"
    const val UrlSocket = "http://190.223.38.245:5000/"

    fun getFecha(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
//        return "05/10/2019"
    }

    fun getHora(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("HH:mm aaa")
        return format.format(date)
    }

    fun getFechaActual(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return format.format(date)
    }

    fun getFechaActualForPhoto(tipo: Int): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        val fechaActual = format.format(date)
        return String.format("%s_%s.jpg", tipo, fechaActual)
    }

    fun getFolder(context: Context): File {
        val folder = File(context.getExternalFilesDir(null)!!.absolutePath)
        if (!folder.exists()) {
            val success = folder.mkdirs()
            if (!success) {
                folder.mkdir()
            }
        }
        return folder
    }

    private fun getDateTimeFormatString(date: Date): String {
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a")
        return df.format(date)
    }

    fun getVersion(context: Context): String {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return pInfo.versionName
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    fun getImei(context: Context): String {
        val deviceUniqueIdentifier: String
        val telephonyManager: TelephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        deviceUniqueIdentifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager.imei
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.deviceId
        }
        return deviceUniqueIdentifier
    }

    fun snackBarMensaje(view: View, mensaje: String) {
        val mSnackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_SHORT)
        mSnackbar.setAction("Ok") { mSnackbar.dismiss() }
        mSnackbar.show()
    }

    fun toastMensaje(context: Context, mensaje: String, b: Boolean) {
        Toast.makeText(context, mensaje, if (b) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }

//    fun dialogMensaje(context: Context, title: String, mensaje: String) {
//        MaterialAlertDialogBuilder(context)
//            .setTitle(title)
//            .setMessage(mensaje)
//            .setPositiveButton("Entendido") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard(edit: EditText, context: Context) {
        edit.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun deletePhoto(photo: String, context: Context) {
        val f = File(getFolder(context), photo)
        if (f.exists()) {
            f.delete()
        }
    }

    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    fun getMobileDataState(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val cmClass = Class.forName(cm.javaClass.name)
        val method = cmClass.getDeclaredMethod("getMobileDataEnabled")
        method.isAccessible = true // Make the method callable
        // get the setting for "mobile data"
        return method.invoke(cm) as Boolean
    }

    fun createImageFile(name: String, context: Context): File {
        return File(getFolder(context), name).apply { absolutePath }
    }

    fun getLocationName(
        context: Context,
        input: TextInputEditText,
        input2: TextInputEditText,
        latitude: Double,
        longitude: Double,
        progressBar: ProgressBar,
        tipo: Int
    ) {
        try {
            val addressObservable = Observable.just(
                Geocoder(context)
                    .getFromLocation(
                        latitude, longitude, 1
                    )[0]
            )
            addressObservable.subscribeOn(Schedulers.io())
                .delay(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Address> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(address: Address) {
                        input.setText(address.getAddressLine(0).toString())
                        if (tipo != 2) {
                            input2.setText(address.locality.toString())
                        }
                    }

                    override fun onError(e: Throwable) {
                        toastMensaje(context, context.getString(R.string.try_again), true)
                        progressBar.visibility = View.GONE
                    }

                    override fun onComplete() {
                        progressBar.visibility = View.GONE
                    }
                })
        } catch (e: IndexOutOfBoundsException) {
            toastMensaje(context, e.toString(), true)
            progressBar.visibility = View.GONE
        }
    }

    //uso de galeria
    fun getFilesFromGallery(
        size: Int, usuarioId: Int, context: Context, data: Intent,
        direccion: String, distrito: String, toPdf: Boolean
    ): Observable<ArrayList<String>> {
        return Observable.create {
            val imagesEncodedList = ArrayList<String>()
            if (data.clipData != null) {
                val mClipData: ClipData? = data.clipData
                val cantidad = mClipData!!.itemCount
                if (cantidad > size) {
                    it.onError(Throwable("No puedes seleccionar mas de $size Imagenes"))
                    it.onComplete()
                    return@create
                }

                for (i in 0 until mClipData.itemCount) {
                    val item: ClipData.Item = mClipData.getItemAt(i)
                    val uri: Uri = item.uri
                    uri.let { returnUri ->
                        context.contentResolver.query(returnUri, null, null, null, null)
                    }?.use { cursor ->
                        cursor.moveToFirst()
                    }

                    val file = getFechaActualForPhoto(usuarioId)
                    val f = File(getFolder(context), file)

                    val input =
                        context.contentResolver.openInputStream(uri) as FileInputStream
                    val out = FileOutputStream(f)
                    val inChannel = input.channel
                    val outChannel = out.channel
                    inChannel.transferTo(0, inChannel.size(), outChannel)
                    input.close()
                    out.close()
                    compressImage(context, f.absolutePath, direccion, distrito, toPdf, file)
                    imagesEncodedList.add(file)
                }
                it.onNext(imagesEncodedList)
                it.onComplete()
                return@create
            } else {
                if (data.data != null) {
                    data.data?.let { returnUri ->
                        context.contentResolver.query(returnUri, null, null, null, null)
                    }?.use { cursor ->
                        cursor.moveToFirst()
                    }

                    val file = getFechaActualForPhoto(usuarioId)
                    val f = File(getFolder(context), file)

                    val input =
                        context.contentResolver.openInputStream(data.data!!) as FileInputStream
                    val out = FileOutputStream(f)
                    val inChannel = input.channel
                    val outChannel = out.channel
                    inChannel.transferTo(0, inChannel.size(), outChannel)
                    input.close()
                    out.close()

//                    getAngleImage(context, f.absolutePath, direccion, distrito, 0)
                    compressImage(context, f.absolutePath, direccion, distrito, toPdf, file)
                    imagesEncodedList.add(file)

                    it.onNext(imagesEncodedList)
                    it.onComplete()
                    return@create
                }
            }
        }
    }

    fun generatePdfFile(
        nameImg: String, context: Context,
        direccion: String, distrito: String,
        id: Int, toPdf: Boolean
    ): Observable<OtDetalle> {
        return Observable.create {
            val f = File(getFolder(context), nameImg)
            if (f.exists()) {
                compressImage(context, f.absolutePath, direccion, distrito, toPdf, nameImg)
                val t = OtDetalle()
                t.otId = id
                t.tipoMaterialId = 24
                t.tipoTrabajoId = 6
                t.nombreTipoMaterial = "Archivos de Viaje Indebido"
                t.viajeIndebido = 1
                t.estado = 2

                val photo = OtPhoto()
                photo.nombrePhoto = nameImg
                photo.urlPhoto = nameImg
                photo.urlPdf = "${nameImg.substring(0, nameImg.length - 4)}.pdf"
                photo.toPdf = true
                photo.estado = 1
                photo.otId = id
                val fotos = ArrayList<OtPhoto>()
                fotos.add(photo)
                t.photos = fotos

                it.onNext(t)
                it.onComplete()
                return@create
            }
            it.onError(Throwable("No se encontro la foto fisica favor de volver a tomar foto"))
            it.onComplete()
        }
    }

    fun generatePhoto(
        nameImg: String, context: Context,
        direccion: String, distrito: String,
        id: Int, toPdf: Boolean
    ): Observable<OtPhoto> {
        return Observable.create {
            val f = File(getFolder(context), nameImg)
            if (f.exists()) {
                compressImage(context, f.absolutePath, direccion, distrito, toPdf, nameImg)
                val photo = OtPhoto()
                photo.otDetalleId = id
                photo.nombrePhoto = nameImg
                photo.urlPhoto = nameImg
                photo.estado = 1

                if (toPdf) {
                    photo.urlPdf = "${nameImg.substring(0, nameImg.length - 4)}.pdf"
                    photo.toPdf = true
                }

                it.onNext(photo)
                it.onComplete()
                return@create
            }
            it.onError(Throwable("No se encontro la foto fisica favor de volver a tomar foto"))
            it.onComplete()
        }
    }

    private fun compressImage(
        context: Context,
        filePath: String,
        direccion: String,
        distrito: String,
        toPdf: Boolean,
        imagePath: String
    ) {
        var scaledBitmap: Bitmap?

        val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            when {
                imgRatio < maxRatio -> {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                }
                imgRatio > maxRatio -> {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                }
                else -> {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow Android to claim the bitmap memory if it runs low on memory
//        options.inPurgeable = true
//        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)

        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

        // check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
//            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            when (orientation) {
                6 -> matrix.postRotate(90f)
                3 -> matrix.postRotate(180f)
                8 -> matrix.postRotate(270f)
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (!toPdf) {
            val canvasPaint = Canvas(scaledBitmap!!)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.WHITE
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)

            val gText = String.format(
                "%s\n%s\n%s",
                getDateTimeFormatString(Date(File(filePath).lastModified())),
                direccion,
                distrito,
            )

            val bounds = Rect()
            var noOfLines = 0
            for (line in gText.split("\n").toTypedArray()) {
                noOfLines++
            }

            paint.getTextBounds(gText, 0, gText.length, bounds)
            val x = 10f
            var y: Float = (scaledBitmap.height - bounds.height() * noOfLines).toFloat()

            // Fondo
            val mPaint = Paint()
            mPaint.color = ContextCompat.getColor(context, R.color.transparentBlack)

            // Tamaño del Fondo
            val top = scaledBitmap.height - bounds.height() * (noOfLines + 1)
            canvasPaint.drawRect(
                0f,
                top.toFloat(),
                scaledBitmap.width.toFloat(),
                scaledBitmap.height.toFloat(),
                mPaint
            )

            // Agregando texto
            for (line in gText.split("\n").toTypedArray()) {
                val txt =
                    TextUtils.ellipsize(
                        line, TextPaint(),
                        (scaledBitmap.width * 0.95).toFloat(),
                        TextUtils.TruncateAt.END
                    )
                canvasPaint.drawText(txt.toString(), x, y, paint)
                y += paint.descent() - paint.ascent()
            }
        }

        val out: FileOutputStream?
        try {
            out = FileOutputStream(filePath)
//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }


        if (toPdf) {
            generatePdf(context, imagePath)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    private fun generatePdf(context: Context, nameImg: String) {
        val file = File(getFolder(context), nameImg)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)

        val ancho = bitmap.width + (bitmap.width * 0.05)
        val alto = bitmap.height + (bitmap.height * 0.05)
        val pdfDocument = PdfDocument()
        val myPageInfo = PdfDocument.PageInfo.Builder(ancho.toInt(), alto.toInt(), 1).create()
        val page = pdfDocument.startPage(myPageInfo)

        val left = (ancho / 2 - ancho * 0.05 / 2) * 0.05
        val top = (alto / 2 - alto * 0.05 / 2) * 0.05

        page.canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), Paint())
        pdfDocument.finishPage(page)

        val filePdf = "${nameImg.substring(0, nameImg.length - 4)}.pdf"
        val myPDFFile = File(getFolder(context), filePdf)
        pdfDocument.writeTo(FileOutputStream(myPDFFile))
        pdfDocument.close()
    }
}