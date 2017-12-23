package xyz.crabfish.nfccard.activity;
import xyz.crabfish.nfccard.service.FileUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;
import xyz.crabfish.nfccard.R;

/**
 * Created by hasee on 2017/12/22.
 */


public class FileTransport extends UniversalActivity implements NfcAdapter.OnNdefPushCompleteCallback, View.OnClickListener {
    private SwipeBackActivityHelper swipeBackActivityHelper;
    private final static String TAG = "File Beam";

    private TextView mSizeTextView = null;
    private TextView mFileNameTextView = null;
    private TextView mContentTypeTextView = null;
    private TextView mStatusTextView = null;
    private TextView mBeamingTextView = null;
    private ImageView mIconImageView = null;
    private Button mFinishButton = null;
    private static int mRequestCode = 1001;
    private NfcAdapter mNfcAdapter;

    public static Intent newIntent(Context context, String title) {
        Intent intent = new Intent(context, FileTransport.class);
        intent.putExtra("title", title);
        return intent;
    }

    private void loadViews() {
        mSizeTextView = (TextView) findViewById(R.id.fileSizeTextView);
        mFileNameTextView = (TextView) findViewById(R.id.fileNameTextView);
        mContentTypeTextView = (TextView) findViewById(R.id.contentTypeTextView);
        mStatusTextView = (TextView) findViewById(R.id.statusTextView);
        mBeamingTextView = (TextView) findViewById(R.id.beamingFileTextView);
        mIconImageView = (ImageView) findViewById(R.id.defaultIconView);
        mFinishButton =(Button)findViewById(R.id.finishButton);
    }

    private void setNfcNotAvailable() {
		mSizeTextView.setVisibility(View.INVISIBLE);
		mFileNameTextView.setVisibility(View.INVISIBLE);
		mContentTypeTextView.setVisibility(View.INVISIBLE);

        mStatusTextView.setText(R.string.nfc_not_available);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_main);
        swipeBackActivityHelper = new SwipeBackActivityHelper(this);
        swipeBackActivityHelper.onActivityCreate();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getIntent().getStringExtra("title"));
        loadViews();


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            setNfcNotAvailable();
            return;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                mStatusTextView.setText(R.string.nfc_turned_off);
            }
        }
      //  processIntent(getIntent());
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, mRequestCode);
        mFileNameTextView.setSelected(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
       // super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == mRequestCode && resultCode == RESULT_OK)
        {

            Uri[] uriList = new Uri[] {data.getData()};
            //mFileNameTextView.setText((CharSequence) data.getData());


           /* if (uriList.length == 1) {*/
                Uri fileUri = uriList[0];
                String filePathFromUri = FileUtils.getImageAbsolutePath(this, fileUri);
                mFileNameTextView.setText(FileUtils.getFileNameByUri(filePathFromUri));
                File file = new File(filePathFromUri);
                mSizeTextView.setText(FileUtils.humanReadableByteCount(file.length(), true));
                mContentTypeTextView.setText(FileUtils.getMimeType(fileUri.getPath()));
            /*} else {
                mFileNameTextView.setText(R.string.multiple_files);
                mContentTypeTextView.setText(R.string.multiple_file_types);
            }*/

            mNfcAdapter.setBeamPushUris(uriList, this);
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

            mFinishButton.setText("停止发送");
            mFinishButton.setTextSize(14);
            mFinishButton.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      //  getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        runOnUiThread(new Runnable() {
            public void run() {
                mStatusTextView.setText(R.string.file_sent);
            }
        });
    }

    private void processIntent(Intent intent) throws IOException {
        Bundle extras = intent.getExtras();
        String action = intent.getAction();

        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                Uri fileUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

                Log.d(TAG, fileUri.toString());

               // mFileNameTextView.setText(FileUtils.getFileNameByUri(this, fileUri));
                mSizeTextView.setText(FileUtils.getHumanReadableFileSize(fileUri.getPath()));
                mContentTypeTextView.setText(intent.getType());

                final Drawable icon = FileUtils.getImageForFile(this, fileUri, intent);
                if (icon != null)
                    mIconImageView.setImageDrawable(icon);

                mNfcAdapter.setBeamPushUris(new Uri[] {fileUri}, this);
                mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
            } else {
                String text = extras.getCharSequence(Intent.EXTRA_TEXT).toString();
                NdefMessage message;

                mFileNameTextView.setText(text);
                mSizeTextView.setText("");
                mContentTypeTextView.setText("plain/text");
                mBeamingTextView.setText(R.string.beaming_text);

                try {
                    new URL(text);

                    NdefRecord uriRecord = new NdefRecord(
                            NdefRecord.TNF_ABSOLUTE_URI ,
                            text.getBytes(Charset.forName("US-ASCII")),
                            new byte[0], new byte[0]);

                    message = new NdefMessage(uriRecord);
                } catch (MalformedURLException e) {
                    NdefRecord record = FileUtils.createTextRecord(text, Locale.getDefault(), true);
                    message = new NdefMessage(record);
                }

                mNfcAdapter.setNdefPushMessage(message, this);
                mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            mBeamingTextView.setText(R.string.beaming_files);
            ArrayList<Uri> fileUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

            long size = 0;
            for (Uri uri: fileUris) {
                Log.d(TAG, uri.toString());
                size = size + FileUtils.getFileSize(uri.getPath());
            }

            Uri[] uriList = fileUris.toArray(new Uri[0]);

            if (uriList.length == 1) {
                Uri fileUri = uriList[0];
               // mFileNameTextView.setText(FileUtils.getFileNameByUri(this, fileUri));
                mSizeTextView.setText(FileUtils.getHumanReadableFileSize(fileUri.getPath()));
                mContentTypeTextView.setText(FileUtils.getMimeType(fileUri.getPath()));
            } else {
                mFileNameTextView.setText(R.string.multiple_files);
                mSizeTextView.setText(FileUtils.humanReadableByteCount(size, true));
                mContentTypeTextView.setText(R.string.multiple_file_types);
            }

            mNfcAdapter.setBeamPushUris(uriList, this);
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}