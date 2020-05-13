MVEL IDE Tooling
==========

### Getting started
This project uses the build tool [sbt](https://www.scala-sbt.org). To work with this project, make sure you've installed sbt.

#### VS Code
To try out the language tooling in VS Code, make sure the VS Code executable `code` is available on your path and run

`yarn install` inside the `vscode-extension` directory

`sbt vscode` in the root directory, wait for that command to start VSCode. The VSCode instance should show `[Extension Developement Host]` in the title bar.

Create an `example.mvel` file, open it and paste in the following to see the parser error highlighting work:
```java
// I removed all the $ signs in the identifier because apparently the identifier parser regex doesn't allow them.
// I also commented out all the lines with casts in them like: (<TypeToCastTo>) <expression>>
// Because the delta to add support for casts is still missing
output.taxEventGenerationBusinessInfos = (output.taxEventGenerationBusinessInfos == null ? new java.util.ArrayList() : output.taxEventGenerationBusinessInfos);
LocalDateTime taxEventsEffectiveWindowStartDateUTC = input.taxEventsWindowStartDateUTC;
if (taxEventsEffectiveWindowStartDateUTC < firstFilingStartDate){
	taxEventsEffectiveWindowStartDateUTC = firstFilingStartDate;
}
DateRange taxEventGenerationWindow = DateRange.builder().startDateUTC(taxEventsEffectiveWindowStartDateUTC).endDateUTC(input.taxEventsWindowEndDateUTC).build();
ArrayList dataPeriods = new java.util.ArrayList();

// Commented out because the [ReturnType.VAT] syntax isn't supported.
// if (input.sellerJurisdictionTaxConfig.applicableReturnsConfigMap[ReturnType.VAT].filingFrequencyInMonths.equals(3)){
    dataPeriods.addAll(TaxEventGenerationUtility.generateDataPeriodsForWindow(taxEventGenerationWindow, "JANUARY-MARCH;APRIL-JUNE;JULY-SEPTEMBER;OCTOBER-DECEMBER;"));
// }
// else if (input.sellerJurisdictionTaxConfig.applicableReturnsConfigMap[ReturnType.VAT].filingFrequencyInMonths.equals(1)){
    dataPeriods.addAll(TaxEventGenerationUtility.generateDataPeriodsForWindow(taxEventGenerationWindow, "JANUARY-JANUARY;FEBRUARY-FEBRUARY;MARCH-MARCH;APRIL-APRIL;MAY-MAY;JUNE-JUNE;JULY-JULY;AUGUST-AUGUST;SEPTEMBER-SEPTEMBER;OCTOBER-OCTOBER;NOVEMBER-NOVEMBER;DECEMBER-DECEMBER;"));
// }

// String taxOfficeState = ((SellerDETaxConfig) input.getSellerJurisdictionTaxConfig()).getTaxOfficeInfo().getAddress.getStateOrRegion();
// String steuernummer = ((SellerDETaxConfig) input.getSellerJurisdictionTaxConfig()).getSteuernummer();

String taxOfficeNumber = null;
String districtNumber = null;
String pin = null;

if (taxOfficeState.equals("Berlin")) {
    taxOfficeNumber = steuernummer.substring(0,2);
    districtNumber = steuernummer.substring(2,5);
    pin = steuernummer.substring(5);
}
else if (taxOfficeState.equals("Nordrhein-Westfalen")){
    taxOfficeNumber = steuernummer.substring(0,3);
    districtNumber = steuernummer.substring(3,7);
    pin = steuernummer.substring(7);
}
String standardSteuernummer = taxOfficeNumber + "/" + districtNumber + "/" + pin;
TaxEventGenerationBusinessInfo tempTaxEvent = null;
String filingMonthMM = null;
String filingYear = null;
// Commented out because a bug in the parser that causes this to Stack overflow. Will look into it.
for(int i = 0; i < dataPeriods.size(); i++){
	// filingMonthMM = ((DateRange) dataPeriods[i]).endDateUTC.format(DateTimeFormatter.ofPattern("MM"));
	// filingYear = String.valueOf(((DateRange) dataPeriods[i]).endDateUTC.year);
	// taxEventsEffectiveWindowStartDateUTC = ((DateRange) dataPeriods[i]).startDateUTC;

	if (taxEventsEffectiveWindowStartDateUTC < firstFilingStartDate) {
		taxEventsEffectiveWindowStartDateUTC = firstFilingStartDate;
    }

	output.taxEventGenerationBusinessInfos.add(tempTaxEvent);
}
```
