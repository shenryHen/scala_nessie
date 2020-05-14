# scala_nessie

## How to run this project from scratch

1. Install [SBT](https://www.scala-sbt.org/download.html)
2. Install the [Scala runtime](https://www.scala-lang.org/download/). Please see the `Other ways to install Scala` if on Mac.
3. Clone this repo
4. In the project's root directory, run this:
  1. `sbt clean`. This removes any pre-built jars accidentally included in this repo, or created from previous runs.
  2. `sbt assembly`. This will build one jar that you can run by itself, with all dependencies included.
5. Run the jar with `scala .\target\scala-2.12\scala_nessie-assembly-1.0.0.jar`. This file path to the jar will generally be the same, 
but the jar name might change for different versions of this project.

## Output
The first 2 object that are printed are the last item on the first page of the ATM endpoint, and the first item on the second page of the endpoint.
The results come from calling a GET to the `/atm`.

New customers, accounts, bills and deposits are created by calling a POST to the respective API endpoints.
1. new custoemr calls `/customers`
2. new account calls `/customers/{customerID}/accounts`
3. new bills calls `/accounts/{accontID}/bills`

The app calls a GET to the `/enterprise/merchants/{merchantID}` endpoint. It prints on the json object that represents the merchant.

Finally, the app calls a DELETE to the `/data?type=Accounts` endoint, deleting all accounts.
