package ua.memberloader;

import ua.memberloader.dao.MongoDB;
import ua.memberloader.dao.UserMongoDB;
import ua.memberloader.worker.Worker;
import ua.memberloader.worker.jobs.LikeLoaderJob;
import ua.memberloader.worker.jobs.UserLoaderJob;

import java.io.IOException;
import java.net.URISyntaxException;;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class AppStarter {

    /*
    https://oauth.vk.com/authorize?client_id=5909285&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=group&response_type=token&v=5.85&state=123456&revoke=0
     */

    public static String TOKEN = "422f0aba0be34136ef8e65e72338071e73b6233a3d21d4b5adad7e1865fa0b28d199b6deed45a009b4bda";

    public static void main(String[] args) throws Exception {
        AppStarter app = new AppStarter();
        app.start();

    }

    private void start() throws URISyntaxException, IOException {

        long start = new Date().getTime();
        MongoDB mongoDB = new MongoDB("localhost",27017, "vk");

        UserMongoDB userDB = new UserMongoDB(mongoDB, "users");
        Worker worker = new Worker();
        CountDownLatch countDownLatch = new CountDownLatch(2);

        for(int i = 0; i < countDownLatch.getCount(); i++){
            worker.addJob(new UserLoaderJob(countDownLatch, userDB));
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        countDownLatch = new CountDownLatch(2);
        System.out.println("Start collect users like");
        for(int i = 0; i < countDownLatch.getCount(); i++){
            worker.addJob(new LikeLoaderJob(userDB, countDownLatch));
        }


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        worker.shutdown();

        long end = new Date().getTime();

        System.out.println((end - start) / 1000/60);
    }

}
