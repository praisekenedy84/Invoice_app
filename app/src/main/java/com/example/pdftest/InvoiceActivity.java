package com.example.pdftest;

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

import androidx.appcompat.app.AppCompatActivity;

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

public class InvoiceActivity extends AppCompatActivity {
    private static final String PLACEHOLDER = "N/A";
    Button btnCreatePDF;
    Spinner txtCur;
    EditText txtAmount;
    EditText txtNumero;
    EditText txtName;
    EditText txtVar;
    EditText txtPrice;
    EditText txtEmail;
    String variable = "5915";
    String amtStr;
    String priceStr;
    String numeroStr;


    private Button generateQrBtn;
    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
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
        txtNumero = findViewById(R.id.edit_numero);
        txtEmail = findViewById(R.id.edit_email);
    }

    // Leaving current layout
    public void leaveLayout(View v) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
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

        // getting width and height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // 595*842
        int leftspace = 34;

        // Header
        canvas.drawText("FAKTURA - DAŇOVÝ DOKLAD", leftspace, 45, paint);
        numeroStr = txtNumero.getText().toString();
        if (numeroStr.isEmpty() || !numeroStr.matches("\\d+")) {
            numeroStr = "1"; // Default value when empty or not a valid number
        }
        DateFormat df = new SimpleDateFormat("yyMMdd");
        String dateCode = df.format(new Date());
        canvas.drawText(dateCode+numeroStr, 490, 45, paint);
        canvas.drawLine(leftspace, 60, 566,60, paint);
        canvas.drawText("Dodavatel", leftspace, 90, paint);
        canvas.drawText("Odběratel", 325, 90, paint);

        // Rectangle bordering
        RectF drawRect = new RectF();
        drawRect.set(leftspace, 107, 310,310);
        canvas.drawRect(drawRect, borderPaint);
        drawRect.set(325, 107, 566,310);
        canvas.drawRect(drawRect, borderPaint);

        // Supplier
        int tableftspace = leftspace + 10;
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("User", "Not set yet");
        canvas.drawText(userName, tableftspace, 130, smallpaintbold);
        String street = sharedPreferences.getString("Street", "Not set yet");
        canvas.drawText(street, tableftspace, 150, smallpaint);
        String city = sharedPreferences.getString("City", "Not set yet");
        canvas.drawText(city, tableftspace, 170, smallpaint);
        String country = sharedPreferences.getString("Country", "Not set yet");
        canvas.drawText(country, tableftspace, 190, smallpaint);
        String ico = sharedPreferences.getString("ICO", "Not set yet");
        canvas.drawText("IČO:  " + ico, tableftspace, 210, smallpaint);
        String dic = sharedPreferences.getString("DIC", "Not set yet");
        canvas.drawText("DIČ:  " + dic, tableftspace, 230, smallpaint);
        String iban = sharedPreferences.getString("IBAN", "Not set yet");
        canvas.drawText("IBAN: "  + iban, tableftspace, 250, smallpaint);
        String bankAcc = sharedPreferences.getString("BankAcc", "Not set yet");
        canvas.drawText( "Bank. spoj.: " + bankAcc, tableftspace, 270, smallpaint);


        // Supplied
        String customerName = txtName.getText().toString();
        canvas.drawText(customerName, 335, 130, smallpaintbold);

        // QR Info
        String curStr = "CZK";// txtCur.getSelectedItem().toString();
        amtStr = txtAmount.getText().toString();
        if (amtStr.isEmpty() || !amtStr.matches("\\d+(\\.\\d+)?")) {
            amtStr = "1"; // Default value when empty or not a valid number
        }
        priceStr = txtPrice.getText().toString();
        if (priceStr.isEmpty() || !priceStr.matches("\\d+")) {
            priceStr = "500"; // Default value when empty or not a valid number
        }

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
        canvas.drawText(amtStr != null ? amtStr : "1", leftspace+80, newline, tinypaint);
        canvas.drawText(priceStr != null ? priceStr : "500", leftspace+140, newline, tinypaint);
        Float finalCost = Float.parseFloat(amtStr) * Float.parseFloat(priceStr);
        canvas.drawText(Float.toString(finalCost), leftspace+270, newline, tinypaint);
        canvas.drawText(Float.toString(finalCost), leftspace+360, newline, tinypaint);
        canvas.drawText(Float.toString(finalCost), leftspace+440, newline, tinypaint);
        canvas.drawText("0%", leftspace+500, newline, tinypaint);

        //Summary
        drawRect.set(150, 650, 450 ,680);
        canvas.drawRect(drawRect, borderPaint);
        canvas.drawText("Cena celkem: "+ Float.toString(finalCost) + " CZK", 170, 673, paint);

        // QR code
        //String varStr = txtVar.getText().toString();
        String inputCode = "SPD*1.0*ACC:" + iban + "*AM:" + Float.toString(finalCost) + ".00*CC:"+ curStr;
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

        int qrtop = 320;
        int qrbottom = 500;
        int qrleft = 386;
        int qrright = 566;
        canvas.drawLine(qrleft, qrtop,qrright,qrtop, paint);
        canvas.drawLine(qrleft, qrtop,qrleft,qrbottom, paint);
        canvas.drawLine(qrright, qrtop,qrright,qrbottom, paint);
        canvas.drawLine(qrleft, qrbottom,440,qrbottom, paint);
        canvas.drawLine(510, qrbottom,qrright,qrbottom, paint);
        canvas.drawText("QR Platba", 450, 505, tinypaint);
        canvas.drawBitmap(qrBitmap,new Rect((int) (qrBitmap.getWidth()*0.1), (int) (qrBitmap.getHeight()*0.1), (int) (qrBitmap.getWidth()*0.9),(int) (qrBitmap.getHeight()*0.9)), new Rect(396,330,556,490) , null);


        document.finishPage(page);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "Faktura_"+customerName.trim()+"_"+dateCode+numeroStr+".pdf";
        File file = new File(downloadsDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(this, "Saved successfully to documents directory", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d("mylog", "Error while writing " + e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //new SendEmailTask(getApplicationContext()).execute();
    }
    // missing handle cases for missing permissions, TODO

}

/*
class SendEmailTask extends AsyncTask<Void, Void, Void> {
    private Context context;

    public SendEmailTask(Context context) {
        this.context = context;
    EditText txtEmail;
    
    private void sendEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        //final String recipientEmail = sharedPreferences.getString("RecipientEmail", "Not set yet");
        final String recipientEmail = txtEmail.getText().toString();
        final String SENDER_EMAIL = sharedPreferences.getString("SenderEmail", "Not set yet");
        final String PASSWORD = sharedPreferences.getString("Password", "Not set yet");
        final String subject = "Faktura";
        final String messageBody = "Dobrý den";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Use the SMTP server of your email provider
        props.put("mail.smtp.port", "587"); // Port for TLS

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);
            //Toast.makeText(this, "Email sent successfully", Toast.LENGTH_SHORT).show();
            // Email sent successfully
        } catch (MessagingException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Email not sent", Toast.LENGTH_SHORT).show();
            // Handle the exception (e.g., show an error message)
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Perform network-related operation (send email) here
        sendEmail();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Handle UI updates after the task is complete (if needed)
    }
}
 */

