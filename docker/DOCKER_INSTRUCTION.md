## **Instructions for Running a MySQL Database in Docker and Connecting with DataGrip**

### **1. Install Docker**

#### **Windows**

* Download and install **Docker Desktop
  **: [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)
* Enable the **WSL 2 backend** if you are using WSL.

#### **WSL / Linux**

* Install Docker Engine and docker-compose.
  **Example for Ubuntu:**

```bash
sudo apt update
sudo apt install docker.io docker-compose -y
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
```

* Log out and log back in for the docker group changes to take effect.

---

### **2. Navigate to the folder containing docker-compose.yml**

Your `docker-compose.yml` file is located in the `project_root/docker` folder. In the terminal (cmd, PowerShell,
Linux/WSL Terminal), run:

```bash
cd /path/to/project_root/docker
```

> **Note:** Replace `/path/to/project_root/docker` with the actual path to the folder on your computer.
> You are now in the folder containing `docker-compose.yml`, from which you will start the database container.

---

### **3. Start the database**

```bash
docker compose up -d
```

Check the running container:

```bash
docker ps
```

You should see the `mysql-local` container with STATUS `Up`.

---

### **4. Connecting to the database from your application**

* Host: `localhost`
* Port: `3306`
* Database: `librarby_testdb`
* User: `librarby_test_user`
* Password: `librarby_test_user_password`

Example JDBC URL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/librarby_testdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=librarby_test_user
spring.datasource.password=librarby_test_user_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

---

### **5. Connecting the database with DataGrip**

1. Open **DataGrip**.

2. Click **Add Data Source → MySQL**.

3. Fill in the fields:

    * **Host:** `localhost`
    * **Port:** `3306`
    * **User:** `librarby_test_user`
    * **Password:** `librarby_test_user_password`
    * **Database:** `librarby_testdb`

4. Click **Test Connection**.

    * If everything is configured correctly, DataGrip will connect to the Docker database.

5. Confirm and save the connection – now you can browse tables, run queries, etc.

> ⚠️ **Note:** If you are using WSL 2, `localhost` works only if DataGrip runs on Windows and the container runs in WSL.
> If DataGrip runs in Linux/WSL, `localhost` still works.

---

### **6. Stopping the container**

```bash
docker-compose down
```

---

### **7. Removing the volume**

If you want to completely delete the database:

```bash
docker-compose down -v
```

Or manually:

```bash
docker volume rm project_root_docker_mysql_data
```

---

### **8. Additional notes**

* On WSL/Ubuntu/Linux, an application running on the same system connects via `localhost`.
* If your application runs in **another container** within the same `docker-compose`, the host should be `mysql`.

