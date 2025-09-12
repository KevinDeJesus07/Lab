/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Core.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Esta clase se encarga de leer archivos CSV y transformarlos en una lista de
 * objetos de tipo {@link Cell}. El objetivo es cargar los datos del dataset de
 * temperaturas y países, procesarlos y dejarlos listos para ser usados en el
 * AVL.
 *
 * El CSV debe tener la siguiente estructura de columnas: ID | Country | ISO3 |
 * valores de temperatura por año...
 *
 * - La primera fila (cabecera) siempre se ignora. - Se soportan comillas dobles
 * en los campos de texto. - Si una celda numérica está vacía o mal escrita, se
 * reemplaza por 0.0.
 *
 * @author Rashid
 */
public class CSVReaderUtil {

    /**
     * Lee un archivo CSV desde la ruta especificada y devuelve una lista de
     * objetos {@link Cell}.
     *
     * @param filePath Ruta del archivo CSV a leer.
     * @return Lista de celdas con la información procesada.
     */
    public static ArrayList<Cell> readCSV(String filePath) {
        ArrayList<Cell> cells = new ArrayList<>();

        try ( BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true; // bandera para saltar la cabecera

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // saltar la cabecera
                }

                // Split "inteligente": separa por comas pero respeta las que están dentro de comillas
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Eliminar comillas sobrantes y espacios en blanco
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].replaceAll("^\"|\"$", "").trim();
                }

                // Primeras 3 columnas fijas: ID, nombre del país y código ISO3
                int objectId = Integer.parseInt(data[0]);
                String country = data[1];
                String iso3 = data[2];

                // Columnas de años (dinámico: desde la 4 en adelante)
                int yearCount = data.length - 3;
                double[] F_i = new double[yearCount];
                for (int i = 0; i < yearCount; i++) {
                    String value = data[i + 3].trim();
                    if (value.isEmpty()) {
                        F_i[i] = 0.0; // valor por defecto si está vacío
                    } else {
                        try {
                            F_i[i] = Double.parseDouble(value);
                        } catch (NumberFormatException e) {
                            F_i[i] = 0.0; // fallback en caso de error numérico
                        }
                    }
                }

                // Crear la celda con ID, país e ISO3
                Cell cell = new Cell(objectId, country, iso3, 0);
                cell.F_i = F_i;

                // Calcular y asignar el promedio FM una sola vez
                cell.FM = cell.averageTemperature();

                // Agregar celda a la lista
                cells.add(cell);
            }
        } catch (IOException e) {
            // En caso de error de lectura, se imprime la traza
            e.printStackTrace();
        }
        
        return cells;
    }
}
