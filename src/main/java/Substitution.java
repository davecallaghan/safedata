class Substitution {
    //substitution
    // for non-reversible, just pick a random name by gender
    // for reversible, take the first three letters of each first name and compile a map. Then map each entry to a random name from the list, using a certain Lew threshold
    // to figure out gender, just try to match the name in the list and pull the gender. Otherwise, coin toss
    // these are first names
    // https://www.ssa.gov/oact/babynames/limits.html
    // these are last names
    //https://fivethirtyeight.datasettes.com/fivethirtyeight/most-2Dcommon-2Dname-2Fsurnames
    // do the same thing for each city and street name

    /**
     *
     * First names
     * The Social Security Administration tracks baby names from 1880 to 2020 here: https://www.ssa.gov/oact/babynames/limits.html
     *
     * From their description of the dataset:
     * National Data on the relative frequency of given names in the population of U.S. births where the individual has a Social Security Number
     * (Tabulated based on Social Security records as of March 7, 2021)
     *
     * For each year of birth YYYY after 1879, we created a comma-delimited file called yobYYYY.txt. Each record in the individual annual files has the format "name,sex,number," where name is 2 to 15 characters, sex is M (male) or F (female) and "number" is the number of occurrences of the name. Each file is sorted first on sex and then on number of occurrences in descending order. When there is a tie on the number of occurrences, names are listed in alphabetical order. This sorting makes it easy to determine a name's rank. The first record for each sex has rank 1, the second record for each sex has rank 2, and so forth.
     * To safeguard privacy, we restrict our list of names to those with at least 5 occurrences.
     *
     * I took the download and used the file for the beginning of each decade from 1920 to 2020. I split the dataset by gender and
     * for each gender I filtered out duplicate names. The result provides a unique set of names by gender: 79,266 F and 44,060 M
     * I've kept the names in gender buckets since there are almost 2x more female names than male names and this is not representative
     * of the 50-50 population split. There does not seem to be any reason to conform the gendered first name to an anonymized dataset.
     * There would be a need to identify the gender of the record, which might not be present, and there aren't any analytic reasons
     * that I can think of to do so (sorting, aggregating, clustering, etc.)
     *
     * Last Names (surnames)
     * From five-thirty-eight, I downloaded the entire dataset of last names (151,672)
     * There are a number of columns that provide information on overall popularity, percentages based on ethnicity, etc.
     * Since the purpose of this function is anonymity, I opted not to use this information.
     * My name is David Callaghan. If I am stored as Gertrude Hernandez, I feel pretty anonymized.
     *
     * There is, however, a potential value in creating a synthetic dataset based on gendered names and surnames of similar frequency
     * for certain use cases. For example, Callaghan shares a similar popularity profile as Alderson and
     * James is the closest to David in popularity in the sample year closest to my birth. David Callaghan and James Alderson
     * have a Levenstein Distance of 11 which Gertrude Hernandez has a distance of 15 from my name. A length-preserving simple
     * redaction XXXXX XXXXXXXXX is a Levenstein distance of 14. ldsjbwipurhfpqurp9cwqnrp9rfpeiocfjlejwhriueuhve76gkvt is 50.
     * I am pretty comfortable with using a distance of ~10 with random first and last name combinations since it will pass
     * any data quality checks by default and is programmatically very simple. I just randomly pick a gender,
     * then a first and last name. Three total operations where I can parallelize (gender + first name) and (last name).
     * It is possible to predict based on the first letter used (http://www.nancy.cc/2020/09/10/top-first-letters-of-us-baby-names-2019/),
     * but a coinflip in Java is very fast.
     *
     * https://planetcalc.com/1721/
     *
     */
}
