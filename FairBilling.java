import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//input model (we can add setters and getters but in this case not required)
class InputModel {
	public String time;
	public String userName;
	public String se;

	InputModel(String str) {
		// splitting the each and every line and placing input data in input model
		String[] indiviualData = str.split(" ");
		if (indiviualData.length >= 3) {
			this.time = indiviualData[0];
			this.userName = indiviualData[1];
			this.se = indiviualData[2].toLowerCase();
		}
	}

	public String getUserName() {
		return userName;
	}
}

//output model (we can add setters and getters but in this case not required)
class ResultModel {
	public int session;
	public String userName;
	public int duration;

}

public class FairBilling {
	// null and empty check for input data
	private static boolean isNotBlank(String str) {
		return (str != null && !str.isEmpty() && str.length() > 0);
	}
	
	//find the difference between two times and will return in seconds
	private static int diffTimeData(String t1, String t2) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(t1);
			d2 = format.parse(t2);
		}catch (ParseException e) {
			e.printStackTrace();
		}
		long diff = d2.getTime() - d1.getTime();
		long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
		return (int) Math.max(0, seconds);
	}

	public static void main(String[] args) throws IOException, ParseException {

		List<String> data = Collections.emptyList();
		List<ResultModel> resultData = new ArrayList<ResultModel>();
		if (0 < args.length) {
			// collecting file data line by line and placing that input model and restricting empty and null data to store in input model
			data = Files.lines(Paths.get(args[0])).collect(Collectors.toList());
			List<InputModel> inputModel = data.stream().map(x -> new InputModel(x))
					.filter(t -> isNotBlank(t.userName) && isNotBlank(t.time) && isNotBlank(t.se))
					.collect(Collectors.toList());

			if (inputModel != null && !inputModel.isEmpty()) {
				String firstTimer = inputModel.get(0).time;
				String lastTimer = inputModel.get(inputModel.size() - 1).time;
				
				// Grouping the records based on userName
				 Map<String, List<InputModel>>	groupingByUserName = inputModel.stream().sorted(Comparator.comparing(InputModel::getUserName))
						.collect(Collectors.groupingBy(InputModel::getUserName));
				 
				 // Iterating the Each and every User Group
				for (Map.Entry<String, List<InputModel>> entry : groupingByUserName.entrySet()) {
					ResultModel model = new ResultModel();

					model.userName = entry.getKey();

					List<InputModel> records = entry.getValue();
					List<String> timeMeF = new ArrayList<String>();
					List<String> timeMeL = new ArrayList<String>();
					//Iterating the input data for single user
					for (InputModel record : records) {
						
						//Check if user having a "Start" and place in timeMeF and search for "End"
						if (record.se.equals("start")) {
							model.session += 1;
							timeMeF.add(record.time);
						} 
						// Check if user having a "end" 
						else if (record.se.equals("end")) {
								// If user "start" exist in timeMeF consider 0th object is start in timeMeF and remove once duration find
								if (timeMeF.size() > 0) {
									String f = timeMeF.get(0);
									timeMeF.remove(0);
									model.duration += diffTimeData(f, record.time);
								} // If we are having end without start then we consider duration as we consider as first record as start time
								else {
									model.session += 1;
									model.duration += diffTimeData(firstTimer, record.time);
								}
								timeMeL.add(record.time);
						}
					}
					
						model.duration += timeMeF.stream().map(m -> diffTimeData(m, lastTimer)).mapToInt(Integer::valueOf).sum();
						model.duration += timeMeL.stream().map(m -> diffTimeData(m, firstTimer)).mapToInt(Integer::valueOf).sum();
						resultData.add(model);
				}
			}
			// display result
			for (ResultModel result : resultData) {
				System.out.println(result.userName + " " + result.session + " " + result.duration);
			}
		}
	}

}
