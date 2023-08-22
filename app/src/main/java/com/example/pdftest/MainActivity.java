package com.example.pdftest;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {
    final static int REQUEST_CODE = 1232;
    Button btnCreatePDF;
    Button btnSaveInfo;
    Button personInfo;
    String customerName = "PLACEHOLDER";
    TextInputEditText txtUserName;
    TextInputEditText txtName;
    Spinner txtCur;
    EditText txtAmount;
    EditText txtVar;
    EditText txtPrice;
    String userName;// = "Jan Štýbar";
    String iban = "CZ0827000000001387862362";
    String bankac = "placeholder448";
    String street = "Balcarova 2241/7";
    String city = "Ostrava";
    String country = "CZECH REPUBLIC";
    String ico = "15199891";
    String dic = "CZ295989";
    String variable = "5919195";
    String amtStr = "2";
    String priceStr = "500";



    private Button generateQrBtn;
    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissions();
    }

    private void askPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    public void loadAddingLayout(View v){
        setContentView(R.layout.new_window);
        btnCreatePDF = findViewById(R.id.btnCreatePdf);
        btnCreatePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPDF();
            }
        });
        txtName = findViewById(R.id.edit_user_name);
        //txtCur = findViewById(R.id.spinner_currency);
        txtPrice = findViewById(R.id.num_price);
        txtAmount = findViewById(R.id.num_amount);
        //ImageView imgQR = findViewById(R.id.image_qrc);
    }

    public void leaveLayout(View v){
        setContentView(R.layout.activity_main);
    }

    public void loadInfoLayout(View v){
        setContentView(R.layout.personal_info);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        txtUserName = findViewById(R.id.edit_user_name);
        String oldretrievedValue = sharedPreferences.getString("User", "Not set yet");
        txtUserName.setHint(oldretrievedValue);

        btnSaveInfo = findViewById(R.id.btn_save_info);
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                leaveLayout(v);
            }
        });
    }

    private void saveUserInfo() {
        userName = txtUserName.getText().toString();
        Toast.makeText(this, userName, Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("User", userName);
        editor.apply();

        String retrievedValue = sharedPreferences.getString("User", "Not set yet");
        Toast.makeText(this, retrievedValue, Toast.LENGTH_SHORT).show();
        //txtUserName.setHint("janicka");

    }

    private void createPDF() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.setFakeBoldText(true);
        paint.setStrokeWidth(2);

        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        Paint smallpaint = new Paint();
        smallpaint.setColor(Color.BLACK);
        smallpaint.setTextSize(16);

        Paint smallpaintbold = new Paint();
        smallpaintbold.setFakeBoldText(true);
        smallpaintbold.setColor(Color.BLACK);
        smallpaintbold.setTextSize(16);

        Paint tinypaint = new Paint();
        tinypaint.setColor(Color.BLACK);
        tinypaint.setTextSize(12);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();


        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        //String nameStr = txtName.getText().toString();
        //String curStr = txtCur.getSelectedItem().toString();
        //String amtStr = txtAmount.getText().toString();
        //String priceStr = txtPrice.getText().toString();    ;
        //String varStr = txtVar.getText().toString();
        String inputCode = "SPD*1.0*ACC:" + iban + "*AM:" + amtStr + ".00*CC:";//+ curStr;
        Log.v("EditText", inputCode);
        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(inputCode, null, QRGContents.Type.TEXT, dimen);
        qrgEncoder.setColorBlack(Color.WHITE);
        qrgEncoder.setColorWhite(Color.BLACK);
        //try {
            // getting our qrcode in the form of bitmap.
        Bitmap qrBitmap = qrgEncoder.getBitmap();
        //} catch (WriterException e) {
            // this method is called for
            // exception handling.
         //   Log.e("Tag", e.toString());
       // }

        // 595*842
        int leftspace = 34;

        // Header
        canvas.drawText("FAKTURA - DAŇOVÝ DOKLAD", leftspace, 45, paint);
        DateFormat df = new SimpleDateFormat("yyMMdd");
        String dateCode = df.format(new Date());
        canvas.drawText(dateCode, 490, 45, paint);
        canvas.drawLine(leftspace, 60, 566,60, paint);
        canvas.drawText("Dodavatel", leftspace, 90, paint);
        canvas.drawText("Odběratel", 310, 90, paint);

        // Rectangle bordering
        RectF drawRect = new RectF();
        drawRect.set(leftspace, 107, 294,310);
        canvas.drawRect(drawRect, borderPaint);
        drawRect.set(310, 107, 566,310);
        canvas.drawRect(drawRect, borderPaint);

        // Supplier
        int tableftspace = leftspace+10;
        canvas.drawText(userName, tableftspace, 130, smallpaintbold);
        canvas.drawText(street, tableftspace, 150, smallpaint);
        canvas.drawText(city, tableftspace, 170, smallpaint);
        canvas.drawText(country, tableftspace, 190, smallpaint);
        canvas.drawText("IČO: "+ico, tableftspace, 210, smallpaint);
        canvas.drawText("DIČ: "+dic, tableftspace, 230, smallpaint);
        canvas.drawText(bankac, tableftspace, 245,  tinypaint);
        canvas.drawText(iban, tableftspace, 260, tinypaint);

        // Supplied
        canvas.drawText(customerName, 320, 130, smallpaintbold);

        // QR Payment
        Integer qrtop = 320;
        Integer qrbottom = 500;
        Integer qrleft = 386;
        Integer qrright = 566;
        canvas.drawLine(qrleft, qrtop,qrright,qrtop, paint);
        canvas.drawLine(qrleft, qrtop,qrleft,qrbottom, paint);
        canvas.drawLine(qrright, qrtop,qrright,qrbottom, paint);
        canvas.drawLine(qrleft, qrbottom,440,qrbottom, paint);
        canvas.drawLine(510, qrbottom,qrright,qrbottom, paint);
        canvas.drawText("QR Platba", 450, 505, tinypaint);
        canvas.drawBitmap(qrBitmap, new Rect(100,100,700,700), new Rect(396,330,556,490) , null);

        //Invoice details
        Integer secondleft = leftspace + 180;
        canvas.drawText("Variabilní symbol", leftspace, 350, tinypaint);
        canvas.drawText(variable, secondleft, 350, tinypaint);
        DateFormat date = new SimpleDateFormat("dd.MM.YYYY");
        String todayCode = date.format(new Date());
        canvas.drawText("Datum vytvoření", leftspace, 370, tinypaint);
        canvas.drawText(todayCode, secondleft, 370, tinypaint);
        canvas.drawText("Datum zd. plnění", leftspace, 390, tinypaint);
        canvas.drawText(todayCode, secondleft, 390, tinypaint);
        canvas.drawText("Datum splatnosti", leftspace, 410, tinypaint);
        canvas.drawText(todayCode, secondleft, 410, tinypaint);
        canvas.drawText("Forma úhrady", leftspace, 430, tinypaint);
        canvas.drawText("Bankovní převod", secondleft, 430, tinypaint);

        //Invoice items
        canvas.drawText("Název", leftspace, 530, tinypaint);
        canvas.drawText("Množství", leftspace+80, 530, tinypaint);
        canvas.drawText("Základní cena/MJ", leftspace+140, 530, tinypaint);
        canvas.drawText("Cena bez DPH", leftspace+270, 530, tinypaint);
        canvas.drawText("Cena s DPH", leftspace+360, 530, tinypaint);
        canvas.drawText("Celkem", leftspace+440, 530, tinypaint);
        canvas.drawText("DPH", leftspace+500, 530, tinypaint);
        canvas.drawLine(leftspace, 540, 566,540, paint);
        Integer newline = 530+30;
        canvas.drawText("Doučování", leftspace, newline, tinypaint);
        canvas.drawText(amtStr, leftspace+80, newline, tinypaint);
        canvas.drawText(priceStr, leftspace+140, newline, tinypaint);
        Float finalCost = Float.parseFloat(amtStr) * Integer.parseInt(priceStr);
        canvas.drawText(Float.toString(finalCost), leftspace+270, newline, tinypaint);
        canvas.drawText(Float.toString(finalCost), leftspace+360, newline, tinypaint);
        canvas.drawText(Float.toString(finalCost), leftspace+440, newline, tinypaint);
        canvas.drawText("0%", leftspace+500, newline, tinypaint);

        //Summary
        drawRect.set(150, 650, 450 ,680);
        canvas.drawRect(drawRect, borderPaint);
        canvas.drawText("Cena celkem: "+Float.toString(finalCost) + "CZK", 170, 673, paint);


        document.finishPage(page);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = customerName+".pdf";
        File file = new File(downloadsDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(this, "Written Successfully!!!", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d("mylog", "Error while writing " + e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}