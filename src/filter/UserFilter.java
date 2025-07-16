package filter;

import cli.utils.InputReader;
import model.UserRequest;

public class UserFilter {

	  public UserRequest collect() {
	        UserRequest request = new UserRequest();
	        double maxBudget = InputReader.readDouble("Enter your maximum budget (USD)");
	      

	        request.setMaxBudget(maxBudget);
	     
	        return request;
	    }

}
