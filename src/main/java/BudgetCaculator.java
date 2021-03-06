

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetCaculator {
    private IBudgetRepo budgetRepo;
    private Map<String, Double> mLookUpTable;
    private double totalResult;

    public BudgetCaculator(IBudgetRepo budgetRepo) {

        this.budgetRepo = budgetRepo;
    }

    public double totalAmount(LocalDate start_day, LocalDate end_day) {
        if (isNotSameDay(start_day, end_day)) {

            List<Budget> budgetList = budgetRepo.getAll();

            mLookUpTable = new HashMap<String, Double>();


            if(budgetList.size()>0){
                for(Budget budget : budgetList){
                    LocalDate localFirstDay = createMonthPerDay(budget);
                    double bugetperday = calculateBudgetPerMonth(budget, localFirstDay);
                    mLookUpTable.put(budget.yearMonth,bugetperday);
                }
            }


            //Same month
            if(isSameMonth(start_day, end_day)){
                String startYM = (start_day.getYear()) + "0" + start_day.getMonthValue();
                Double bugetStartMonthperDay = mLookUpTable.get(startYM);
                int days = start_day.lengthOfMonth() - start_day.getDayOfMonth() + 1;
                long diffDate = Duration.between(start_day.atStartOfDay(), end_day.atStartOfDay()).toDays() +1 ;
                if (bugetStartMonthperDay != null) {
                    totalResult = diffDate * bugetStartMonthperDay;
                }

            }else {
                //Check Start day in DB

                String startYM = (start_day.getYear()) + "0" + start_day.getMonthValue();
                Double bugetStartMonthperDay = mLookUpTable.get(startYM);
                int days = start_day.lengthOfMonth() - start_day.getDayOfMonth() + 1;
                totalResult = days * bugetStartMonthperDay;

                String endYM = (end_day.getYear()) + "0" + end_day.getMonthValue();
                Double bugetEndMonthofDay = mLookUpTable.get(endYM);
                int endday = end_day.getDayOfMonth();
                if(bugetEndMonthofDay!=null) {
                    totalResult += endday * bugetEndMonthofDay;
                }

            }


            //for 每個月
//            for (Budget budget : budgetList) {
//
//                // 每天預算
//
//                LocalDate budgetFirstDay = createMonthPerDay(budget);
//
//                double budgeOfDay = calculateBudgetPerMonth(budget, budgetFirstDay);
//
//                long diffDate = Duration.between(start_day.atStartOfDay(), end_day.atStartOfDay()).toDays() +1 ;
//                mLookUpTable.put(budget.yearMonth,budgeOfDay);
//
//                totalResult += budgeOfDay * diffDate;
//                //
//
//            }

            return totalResult;

        }

        return 10;
    }

    private boolean isSameMonth(LocalDate start_day, LocalDate end_day) {
        return start_day.getMonthValue() == end_day.getMonthValue();
    }

    private LocalDate createMonthPerDay(Budget budget) {
        return LocalDate.of(Integer.parseInt(budget.yearMonth.substring(0, 4)), Integer.parseInt(budget.yearMonth.substring(4)), 1);
    }

    private double calculateBudgetPerMonth(Budget budget, LocalDate budgetFirstDay) {
        int daysOfMonth = budgetFirstDay.lengthOfMonth();

        return budget.budgetAmount / daysOfMonth;
    }

    private boolean isNotSameDay(LocalDate start_day, LocalDate end_day) {
        return start_day.compareTo(end_day) != 0;
    }

}
