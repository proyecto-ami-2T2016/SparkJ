package com.github.grantwest.sparkj.Tools;

import java.io.*;

public class SparkCredentials {
    public String deviceId;
    public String username;
    public String password;

    public SparkCredentials(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        deviceId = br.readLine();
        username = br.readLine();
        password = br.readLine();
        br.close();
    }
}
