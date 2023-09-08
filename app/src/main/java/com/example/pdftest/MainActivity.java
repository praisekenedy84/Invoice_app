package com.example.pdftest;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    TextInputEditText txtStreet;
    TextInputEditText txtCity;
    TextInputEditText txtCountry;
    TextInputEditText txtICO;
    TextInputEditText txtDIC;
    TextInputEditText txtIBAN;
    TextInputEditText txtBankAcc;
    TextInputEditText txtSWIFT;
    Spinner txtCur;
    private static final String PLACEHOLDER = "N/A";
    private static final String CHANNEL_ID = "MyNotificationChannel";
    EditText txtAmount;
    EditText txtVar;
    EditText txtPrice;
    String userName;// = "Jan Štýbar";
    String iban = "CZ082700000000484784959";
    String bankAcc = "placeholder448";
    String swift;
    String street;
    String city = "Ostrava";
    String country = "CZECH REPUBLIC";
    String ico = "15199891";
    String dic = "CZ295989";
    String variable = "5919195";
    String amtStr = "2";
    String priceStr = "500";

    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra("ACTION", "YOUR_ACTION");

    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, dialogIntent, PendingIntent.FLAG_UPDATE_CURRENT);


    private Button generateQrBtn;
    QRGEncoder qrgEncoder;

    // Displaying the main layout
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askPermissions();
    }

    // Asking necessary permissions
    private void askPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    // Displaying invoice creation layout
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

    // Leaving current layout
    public void leaveLayout(View v){
        setContentView(R.layout.activity_main);
    }

    // Displaying personal information layout
    public void loadInfoLayout(View v){
        setContentView(R.layout.personal_info);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String retrievedUser = sharedPreferences.getString("User", "Not set yet");
        txtUserName = findViewById(R.id.edit_user_name);
        txtUserName.setHint(retrievedUser);
        String retrievedStreet = sharedPreferences.getString("Street", "Not set yet");
        txtStreet = findViewById(R.id.edit_street_name);
        txtStreet.setHint(retrievedStreet);
        String retrievedCity = sharedPreferences.getString("City", "Not set yet");
        txtCity = findViewById(R.id.edit_city);
        txtCity.setHint(retrievedCity);
        String retrievedCountry = sharedPreferences.getString("Country", "Not set yet");
        txtCountry = findViewById(R.id.edit_country);
        txtCountry.setHint(retrievedCountry);
        String retrievedICO = sharedPreferences.getString("ICO", "Not set yet");
        txtICO = findViewById(R.id.edit_ico);
        txtICO.setHint(retrievedICO);
        String retrievedDIC = sharedPreferences.getString("DIC", "Not set yet");
        txtDIC = findViewById(R.id.edit_dic);
        txtDIC.setHint(retrievedDIC);
        String retrievedBankAcc = sharedPreferences.getString("BankAcc", "Not set yet");
        txtBankAcc = findViewById(R.id.edit_bank_acc);
        txtBankAcc.setHint(retrievedBankAcc);
        String retrievedIBAN = sharedPreferences.getString("IBAN", "Not set yet");
        txtIBAN = findViewById(R.id.edit_iban);
        txtIBAN.setHint(retrievedIBAN);
        String retrievedSWIFT = sharedPreferences.getString("SWIFT", "Not set yet");
        txtSWIFT = findViewById(R.id.edit_swift);
        txtSWIFT.setHint(retrievedSWIFT);


        btnSaveInfo = findViewById(R.id.btn_save_info);
        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                leaveLayout(v);
            }
        });
    }

    // Saving user personal information
    private void saveUserInfo() {
        userName = txtUserName.getText().toString();
        street = txtStreet.getText().toString();
        city = txtCity.getText().toString();
        country = txtCountry.getText().toString();
        ico = txtICO.getText().toString();
        dic = txtDIC.getText().toString();
        bankAcc = txtBankAcc.getText().toString();
        iban = txtIBAN.getText().toString();
        swift = txtSWIFT.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("User", userName);
        editor.putString("Street", street);
        editor.putString("City", city);
        editor.putString("Country", country);
        editor.putString("ICO", ico);
        editor.putString("DIC", dic);
        editor.putString("BankAcc", bankAcc);
        editor.putString("IBAN", iban);
        editor.putString("SWIFT", swift);

        editor.apply();
    }


    public void showNotification(View view) {
        // Create a notification channel (required for Android 8.0 and higher)
        createNotificationChannel();

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.profile_foreground)
                .setContentTitle("Button Pressed")
                .setContentText("You pressed the button and triggered this notification!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Notification Channel";
            String description = "My custom notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Creating and saving the created PDF
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
        int tableftspace = leftspace + 10;
        canvas.drawText(userName != null ? userName : PLACEHOLDER, tableftspace, 130, smallpaintbold);
        canvas.drawText(street != null ? street : PLACEHOLDER, tableftspace, 150, smallpaint);
        canvas.drawText(city != null ? city : PLACEHOLDER, tableftspace, 170, smallpaint);
        canvas.drawText(country != null ? country : PLACEHOLDER, tableftspace, 190, smallpaint);
        canvas.drawText("IČO: " + (ico != null ? ico : PLACEHOLDER), tableftspace, 210, smallpaint);
        canvas.drawText("DIČ: " + (dic != null ? dic : PLACEHOLDER), tableftspace, 230, smallpaint);
        canvas.drawText(bankAcc != null ? bankAcc : PLACEHOLDER, tableftspace, 245, tinypaint);
        canvas.drawText(iban != null ? iban : PLACEHOLDER, tableftspace, 260, tinypaint);


        // Supplied
        canvas.drawText(customerName != null ? customerName : PLACEHOLDER, 320, 130, smallpaintbold);

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
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1); // Add one month

        Date oneMonthFromNow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
        String oneMonthDateString = dateFormat.format(oneMonthFromNow);
        canvas.drawText("Datum splatnosti", leftspace, 410, tinypaint);
        canvas.drawText(oneMonthDateString, secondleft, 410, tinypaint);
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
        canvas.drawText("Cena celkem: "+Float.toString(finalCost) + " CZK", 170, 673, paint);


        document.finishPage(page);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = customerName+".pdf";
        File file = new File(downloadsDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(this, "Saved succesfully to documents directory", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d("mylog", "Error while writing " + e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}