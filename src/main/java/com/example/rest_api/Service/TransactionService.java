package com.example.rest_api.Service;

import com.example.rest_api.Dao.TransactionsDao;
import com.example.rest_api.Dao.UserDao;
import com.example.rest_api.Entities.Transactions;
import com.example.rest_api.Entities.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionsDao transactionDao;

    @Autowired
    UserDao userDao;

    @Autowired
    ResponseService responseService;

    @Autowired
    UserService userService;

    public List<Transactions> getTransactions(String auth){

        String [] userCredentials = userService.getUserCredentials(auth);
        String username = userCredentials[0];
        String password = userCredentials[1];

        Optional<User> optionalUser = userDao.findById(username);
        try{

            User user = optionalUser.get();
            if(userService.checkHash(password,user.password)){

                return user.getTransactions();

            }

        }catch(Exception e){
            return null;
        }
        return null;
    }

    public boolean createTransaction(String auth, Transactions transaction){

        String[] userCredentials = userService.getUserCredentials(auth);

        if(userService.authUser(userCredentials)){
            User user = userDao.getOne(userCredentials[0]);
            try{
                String id = transaction.getCategory() + transaction.getMerchant();

                while(true) {
                    String hashedId = userService.hash(id);
                    if(!ifTransactExists(hashedId)) {
                        transaction.setTransaction_id(hashedId);
                        break;
                    }
                }

                if(transaction.getTransaction_id() != null) {
                    user.addTransaction(transaction);
                    transaction.setUser(user);
                    transactionDao.save(transaction);
                    return true;
                }

            }catch(Exception e){
                return false;
            }
        }

        return false;

    }

    public boolean ifTransactExists(String id){

        Optional<Transactions> optionalTransactions = transactionDao.findById(id);
        try{
            Transactions transact = optionalTransactions.get();
            if(transact != null){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }


}
