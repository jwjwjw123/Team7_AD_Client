package com.example.adteam7.team7_ad_client.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.ListView;

import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adteam7.team7_ad_client.R;
import com.example.adteam7.team7_ad_client.model.AckDeliveryDetails;
import com.example.adteam7.team7_ad_client.model.PendingPODetails;
import com.example.adteam7.team7_ad_client.model.SessionManager;

import com.example.adteam7.team7_ad_client.network.APIDataAgent;
import com.example.adteam7.team7_ad_client.network.APIDataAgentImpl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AcknowledgeDeliveryDetails extends AppCompatActivity {

    boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledge_delivery_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Acknowledge Delivery");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final TextView pono = findViewById(R.id.ackDeliTextViewPono);

        Intent intent = getIntent();
        if (intent.hasExtra("selectedpo")) {
            isNew = false;
            String selectedpo = intent.getStringExtra("selectedpo");

            new AsyncTask<String, Void, List<PendingPODetails>>() {

                APIDataAgent dataAgent = new APIDataAgentImpl();

                @Override
                protected List<PendingPODetails> doInBackground(String... params) {
                    List<PendingPODetails> polist = dataAgent.GetPendingPODetails(params[0]);
                    //Log.i("list",polist.get(0).get("Description"));
                    return polist;
                }

                @Override
                protected void onPostExecute(List<PendingPODetails> polist) {
                    pono.setText(polist.get(0).get("PONo"));
                    Log.i("userID", SessionManager.getInstance().getUserid());
                    Log.i("POID", polist.get(0).get("PONo"));

                    ListView list = (ListView) findViewById(R.id.ackDelilistDetails);
                    SimpleAdapter adapter = new SimpleAdapter(AcknowledgeDeliveryDetails.this, polist,
                            R.layout.ackrowdetails,
                            new String[]{"ItemId", "Description", "Quantity", "Quantity"},
                            new int[]{R.id.ackDeliTextViewItemId, R.id.ackDeliTextViewDescription, R.id.ackDeliTextViewQty, R.id.ackDelieditTextQty});
                    list.setAdapter(adapter);
                    Log.i("userID", SessionManager.getInstance().getUserid());


                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selecteditem = (String) parent.getAdapter().getItem(position);
                            Log.i("Selecteditem", selecteditem);
                        }
                    });

                }
            }.execute(selectedpo);
        }

    }

    public void ConfirmDelivery(View v) {


        final EditText orderNo = findViewById(R.id.ackDeliEditTextDO);
        final ListView list = findViewById(R.id.ackDelilistDetails);



        final APIDataAgent dataAgent = new APIDataAgentImpl();

        Intent intent = getIntent();
        if (intent.hasExtra("selectedpo")) {
            isNew = false;
            String selectedpo = intent.getStringExtra("selectedpo");
            int count = list.getAdapter().getCount();
            AckDeliveryDetails[] delDetails = new AckDeliveryDetails[count];
            ArrayList<String> qty1 = new ArrayList<String>();
            ArrayList<String> item = new ArrayList<String>();
            //String item[] = new String[count];

            for(int j=0;j<count;j++)

            {
                TextView itemID;
                EditText actualQty;
                v = list.getChildAt(j);
                itemID = v.findViewById(R.id.ackDeliTextViewItemId);
                actualQty = v.findViewById((R.id.ackDelieditTextQty));
                //item[j] = itemID.getText().toString();
                item.add(itemID.getText().toString());
                qty1.add(actualQty.getText().toString());

                AckDeliveryDetails delDetails1 = new AckDeliveryDetails(item.get(j),qty1.get(j), "", "",
                        orderNo.getText().toString(), "", SessionManager.getInstance().getUserid(), selectedpo);

                delDetails[j]=delDetails1;
            }
            //list.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

            new AsyncTask<AckDeliveryDetails, Void, Void>() {
                @Override
                protected Void doInBackground(AckDeliveryDetails... params) {

                    dataAgent.ConfirmDO(Arrays.asList(params));

                    Intent homepage = new Intent(AcknowledgeDeliveryDetails.this, AcknowledgeDelivery.class);
                    startActivity(homepage);
                    return null;

                }
            }.execute(delDetails);
            Toast.makeText(AcknowledgeDeliveryDetails.this, "Successful!", Toast.LENGTH_SHORT).show();
            Log.i("po", selectedpo);
        }
    }

}
