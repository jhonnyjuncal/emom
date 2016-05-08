package emom.jhonny.com.emom;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private boolean estado = false;
    private boolean primeraVez = true;
    private int contador = 0;
    private int numeroVeces = 30;
    private View vista = null;

    private Chronometer crono = null;
    private EditText editVeces = null;
    private Button btnMenos = null;
    private Button btnMas = null;
    private Button btnPantalla = null;
    private TextToSpeech textToSpeech = null;
    private FloatingActionButton fab = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // barra de herramientas
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Servicio para mantener la pantalla encendida
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            crono = (Chronometer)findViewById(R.id.chronometer);
            editVeces = (EditText)findViewById(R.id.editText);
            editVeces.setText(numeroVeces + "");

            numeroVeces = new Integer(editVeces.getText().toString());

            btnMenos = (Button) findViewById(R.id.boton_menos);
            btnMenos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numeroVeces--;
                    editVeces.setText(numeroVeces + "");
                }
            });

            btnMas = (Button) findViewById(R.id.boton_mas);
            btnMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numeroVeces++;
                    editVeces.setText(numeroVeces + "");
                }
            });

            btnPantalla = (Button) findViewById(R.id.boton_pantalla);
            btnPantalla.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String texto = btnPantalla.getText().toString();

                    if(texto.equals("SI")) {
                        btnPantalla.setText("NO");
                        System.out.println("Ahora la pantalla se apagara");

                    }else {
                        btnPantalla.setText("SI");
                        System.out.println("Ahora la pantalla estara encendida");
                    }
                }
            });

            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
            });

            System.out.println("** Numero de repeticiones: " + numeroVeces);

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    vista = view;
                    numeroVeces = new Integer(editVeces.getText().toString());

                    contador = numeroVeces;
                    inicioDelCronometro();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void inicioDelCronometro() {
        try {
            long elapse_time = crono.getBase() - SystemClock.elapsedRealtime();

            if (estado) {
                // cuando el cronometro esta encendido
                estado = false;
                crono.stop();
                editVeces.setEnabled(true);
                fab.setImageResource(R.mipmap.play_green);

            } else {
                // cuando el cronometro esta apagado
                estado = true;
                editVeces.setEnabled(false);
                fab.setImageResource(R.mipmap.stop_red);

                if(primeraVez) {
                    crono.setBase(SystemClock.elapsedRealtime());
                }else {
                    crono.setBase(SystemClock.elapsedRealtime() + elapse_time);
                }

                boolean reiniciar = false;
                crono.setBase(SystemClock.elapsedRealtime());
                crono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        String texto = chronometer.getText().toString();
                        String[] valores = texto.split(":");
                        Integer minutos = new Integer(valores[0]);
                        Integer segundos = new Integer(valores[1]);

                        if(minutos == 0 && segundos >= 55) {
                            // poner el texto en rojo
                            crono.setTextColor(Color.RED);

                            String texto_decir = "";
                            switch(segundos) {
                                case 55: texto_decir = "5"; break;
                                case 56: texto_decir = "4"; break;
                                case 57: texto_decir = "3"; break;
                                case 58: texto_decir = "2"; break;
                                case 59: texto_decir = "1"; break;
                            }
                            if(texto_decir != "")
                                speak(texto_decir);

                        }else if(minutos > 0) {
                            contador--;
                            crono.setTextColor(Color.BLACK);
                            crono.stop();
                            ejecutaFinBucle();
                        }
                    }
                });
                crono.start();
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ejecutaFinBucle() {
        try {
            if(contador > 0) {
                //crono.setBase(SystemClock.elapsedRealtime());
                //crono.start();
                crono.setOnChronometerTickListener(null);
                estado = false;
                editVeces.setText(contador + "");

                inicioDelCronometro();

            }else if(contador == 0) {
                editVeces.setText(contador + "");
                crono.setTextColor(Color.GREEN);
                Snackbar.make(this.vista, "Entrenamiento terminado", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void speak(String str) {
        textToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);
        //textToSpeech.setSpeechRate(0.0f);
        //textToSpeech.setPitch(0.0f);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.nav_inicio) {
            intent = new Intent(this, MainActivity.class);
        } else if (id == R.id.nav_gallery) {
            intent = new Intent(this, EnConstruccion.class);
        } else if (id == R.id.nav_slideshow) {
            intent = new Intent(this, EnConstruccion.class);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, EnConstruccion.class);
        } else if (id == R.id.nav_compartir) {
            intent = new Intent(this, EnConstruccion.class);
        } else if (id == R.id.nav_send) {
            intent = new Intent(this, EnConstruccion.class);
        } else if (id == R.id.nav_desarrollador) {
            intent = new Intent(this, EnConstruccion.class);
        } else if (id == R.id.nav_acerca) {
            intent = new Intent(this, EnConstruccion.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        startActivity(intent);

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
    }
}
