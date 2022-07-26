package android.autoinstalls.config.multilaser.appbluetooth;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int ATIVAR = 1;
    private static final int SOLICITA = 2;

    BluetoothAdapter myBluetooth = null;
    BluetoothDevice myDevice = null;
    BluetoothSocket mySocket = null;

    boolean conexao;
    private static String MAC = null;

    Button button, button2, button3, conectar;
    UUID MEU_ID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conectar = (Button)findViewById(R.id.conectar);
        button = (Button)findViewById(R.id.button);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null){
            Toast.makeText(getApplicationContext(),"Bluetooth indisponivel para o modelo", Toast.LENGTH_LONG).show();
        }else if(!myBluetooth.isEnabled()){
            Intent ativaBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ativaBluetooth, ATIVAR);
        }
        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conexao){
                    //desconectar
                    try {
                        mySocket.close();
                        conexao = false;
                        conectar.setText("Conectar");
                        Toast.makeText(getApplicationContext(),"Bluetooth desconectado", Toast.LENGTH_LONG).show();
                    }catch (IOException erro){
                        Toast.makeText(getApplicationContext(),"MOcorreu um erro " + erro, Toast.LENGTH_LONG).show();
                    }
                }else{
                    //conectar
                    Intent abreLista = new Intent(MainActivity.this, ListaDispositivo.class);
                    startActivityForResult(abreLista, SOLICITA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ATIVAR:
                if (resultCode == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(),"Bluetooth ativado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"O Bluetooth não foi ativado, o aplicativo foi encerrado!", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case SOLICITA:
        }
        if (resultCode == Activity.RESULT_OK){
            MAC = data.getExtras().getString(ListaDispositivo.ENDERECO_MAC);
            //Toast.makeText(getApplicationContext(),"MAC: " + MAC, Toast.LENGTH_LONG).show();
            myDevice = myBluetooth.getRemoteDevice(MAC);
            try {
                mySocket = myDevice.createRfcommSocketToServiceRecord(MEU_ID);
                conexao = true;
                mySocket.connect();

                conectar.setText("Desconectar");
                Toast.makeText(getApplicationContext(),"Dispositivo conectado com: " + MAC, Toast.LENGTH_LONG).show();
            }catch (IOException erro){
                conexao = false;
                Toast.makeText(getApplicationContext(),"Ocorreu um erro: " + erro, Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),"Falha ao obter o MAC", Toast.LENGTH_LONG).show();
        }
    }
}