/**
 * 
 */
package edu.incense.android.datatask.filter;

import android.util.Log;
import edu.incense.android.datatask.data.AccelerometerFrameData;
import edu.incense.android.datatask.data.Data;
import edu.incense.android.datatask.data.others.StepsAccData;

public class StepsAccFilter extends DataFilter {
         private static final String ATT_STEPS = "isStepping";
       /* private static final int SHAKE_THRESHOLD = 500; //900;
        private double last_x, last_y, last_z;
        private double lastUpdate;
        private boolean last;
        private int counter;
        */
    
    //Variables de mediciones
        public short n = 10; //Variable para saber cuantos datos se usarán para suavizar al señal.
        public double[] datoSuavizado = new double[n]; //Aquí s eguardara el dato de la señal suavizada.
        public byte primerasMuestras; //Necesario para llenar el vector de datos suavizado al inicio
        public double[] gravedad = new double[3];
        public double[] accLineal = new double[3];
        public double nivel = 1.14351917629104; //1 desviación estandar
        //public double nivel = 0.892897124657045; //2 desviaciones estandar
        public long eventoAnt = 0;
        public short pasos2Seg = -1;
        public double distanciaTrayecto = 0;
        public double calorias = 0;
        public double velProm = 0;
        
        //Para contar Pasos
        public double[] registroDatos = new double[2];
        public short cuentaPasos; //Acumulador para contar pasos
        public long timeAnt; //Timestamp del evento anterior para que no cuente dos eventos muy seguidos
        
        public StepsAccFilter() {
            super();
            setFilterName(this.getClass().getName());
                        
        }

        @Override
        public void start() {
            super.start();
            registroDatos[0] = 10;
            registroDatos[1] = 10;
            
            cuentaPasos=-1;
            timeAnt=0;
                    
          //inicializando variables para suavizar la se�al y quitar la gravedad
            primerasMuestras = 0;
            gravedad[0] = 0.0;
            gravedad[1] = 0.0;
            gravedad[2] = 0.0;
            
            accLineal[0] = 0.0;
            accLineal[1] = 0.0;
            accLineal[2] = 0.0;
            
            datoSuavizado[0] = 0.0;
            datoSuavizado[1] = 0.0;
            datoSuavizado[2] = 0.0;
            datoSuavizado[3] = 0.0;
            datoSuavizado[4] = 0.0;
            datoSuavizado[5] = 0.0;
            datoSuavizado[6] = 0.0;
            datoSuavizado[7] = 0.0;
            datoSuavizado[8] = 0.0;
            datoSuavizado[9] = 0.0;     
                
        }

        @Override
        protected void computeSingleData(Data data) {
            accelerometerData(data);
            //pushToOutputs(newData);
        }

        private void accelerometerData(Data data) {
            
            Log.d(ATT_STEPS, "entrando a accelerometerData");
            AccelerometerFrameData accData = (AccelerometerFrameData) data;
            double[][] frame = accData.getFrame();
            for(int i=0; i<accData.getFrame().length; i++){
                steps(frame[i]);
                 
                
           }
           // data.getExtras().putBoolean(ATT_ISSHAKE, shake);
        }
        
         private void steps(double[] dataAcc) {
            double avgSuavizado = 0; //variable auxiliar para hacer el suavizado de las muestras 
            byte i; // Contador auxiliar apara cualquier ciclo
            double estatura = 1.83;
            double peso = 90.00;
            double distXPaso = 0;
            double factorDiv = 0;
            double[][] strideLenght = {{8, 1}, {6, 1.2}, {5, 2}, {4, 3}, {3, 4}, {2, 5}};
                        
            //VERSION CRUCE POR NIVEL
                final double alpha = 0.8;
                gravedad[0] = alpha * gravedad[0] + (1 - alpha) * dataAcc[0];
                gravedad[1] = alpha * gravedad[1] + (1 - alpha) * dataAcc[1];
                gravedad[2] = alpha * gravedad[2] + (1 - alpha) * dataAcc[2];
                accLineal[0] = dataAcc[0] - gravedad[0];
                accLineal[1] = dataAcc[1] - gravedad[1];
                accLineal[2] = dataAcc[2] - gravedad[2];
                
                
                if (primerasMuestras < 10){
                    datoSuavizado[primerasMuestras] = Math.sqrt(Math.pow(accLineal[0],2) + Math.pow(accLineal[1],2) + Math.pow(accLineal[2],2));
                    avgSuavizado = 0;
                    primerasMuestras++;
                    for (i=0; i<primerasMuestras; i++){
                        avgSuavizado = avgSuavizado + datoSuavizado[i];
                    }
                    avgSuavizado = avgSuavizado / primerasMuestras;
                } else {
                    avgSuavizado = 0;
                    for (i=0; i<(n-1); i++){
                        datoSuavizado[i] = datoSuavizado[i+1];
                        avgSuavizado = avgSuavizado + datoSuavizado[i];
                    }
                    datoSuavizado[i] =  Math.sqrt(Math.pow(accLineal[0],2) + Math.pow(accLineal[1],2) + Math.pow(accLineal[2],2));
                    avgSuavizado = (avgSuavizado + datoSuavizado[i]) / primerasMuestras;
                }
                registroDatos[0] = registroDatos[1];
                registroDatos[1] = avgSuavizado;
                
                if ((registroDatos[0] >= nivel && registroDatos[1] < nivel) && (((long)dataAcc[3] - timeAnt)>=200000000)){
                    cuentaPasos++;
                    //linea = "paso," + "," + String.valueOf(event.timestamp) + "\n";
                //  streamLogAcc.write(linea.getBytes());
                    timeAnt = (long)dataAcc[3];
                    pasos2Seg++;
                }
                
                if (eventoAnt == 0){
                    eventoAnt = (long)dataAcc[3];
                }
                
                if ((((long)dataAcc[3] - eventoAnt) >= 2000000000) && (pasos2Seg > 0)){
                    for (i=0; i<strideLenght.length; i++){
                        if (pasos2Seg < strideLenght[i][0]){
                            factorDiv = strideLenght[i][1];
                        }
                    }
                    if (factorDiv == 0){
                        distXPaso = estatura * 1.2; 
                    } else {
                        distXPaso = estatura / factorDiv;
                    }
                    distanciaTrayecto = distanciaTrayecto + (pasos2Seg * distXPaso);
                    velProm = pasos2Seg * (distXPaso / 2.0);
                    calorias = calorias + (velProm * (peso/1800.0));
                    pasos2Seg = 0;
                    eventoAnt = (long)dataAcc[3];
                    factorDiv = 0;
                }
                // TextView cajaRes = (TextView) findViewById(R.id.textView1);
                Log.d(ATT_STEPS,"Pasos: " + String.valueOf(cuentaPasos) + "\n" + "Velocidad: " + String.valueOf(velProm) + " m/s" + "\n" + "Distancia: " + String.valueOf(distanciaTrayecto) + " m" + "\n" + "Calorias: " + String.valueOf(calorias) + " C" + "\n");
                StepsAccData stepsData = new StepsAccData(cuentaPasos,velProm,distanciaTrayecto,calorias); 
                pushToOutputs(stepsData);
                   
            }
    
}
