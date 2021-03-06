#Mobile App Developer Coding Challenge

## Developer Notes:

### Assumptions/Constraints:

- Input left vs right of the decimal is separated into two fields.  This is just to keep the input error checking as simple as possible.  If I wanted to have it one big input text field, more error checking can be done in an "Error Check" layer before data is passed to the Presenter.
- In the "dollar" field, any non-digit (like a period) will pass the focus onto the cents view.  This is a UX convenience feature and is not explicitly stated on the UI.
- Max input "dollar" value (the number to the left of the decimal) is 2147483647 (+ve 32-bit integer).  Certainly this can be increased if we used a different java.lang.Number like Long. 
- Max number of digits in "cents" capped to 2.  This can also be increased and more error checking would be required.
- If 1 digit is input into the "cents" field, it is assumed to be the 10s digit.  (i.e. ".1" is 10 cents)
- Rates are only fetched when the CONVERT button is pressed.  If rates should be fetched as the user types, debouncing should be added (RxAndroid has a convenient way)
- My git commits may include more than 1 feature and/or bug fix in an effort to reduce the number of commits for this challenge.  In team/production environments, git commits should only include 1 bug fix or 1 feature (depending on size of feature, it may be to be broken down into manageable components)
- Basic unit tests have been included.  For UI UnitTesting, I would have used Espresso.
- Pressing the soft keyboard check/done button on the "cent" field will simply dismiss the keyboard.  Consideration: It can be wired up to start the conversion for a better UX.
- It is somewhat difficult to tell from the UI whether rates have been fetched from cache (< 30 minutes) or refreshed from network.  This can be improved.  But for now, progress indicator means network request.


### Design Decisions:

- The MVP paradigm was used to isolate components with the app to give clarity of responsibility within each layer and activity lifecycle. 
- Retrofit was used since we are dealing with a REST API Web Service.  It makes for parsing JSON data (via GSON) over HTTP much easier, and if asynchronous or synchronous network calls are required later on, it reduces potential of nested networking callbacks (which AsyncTask/Volley could present)
- RxAndroid was used to reduce complexities with the architecture and implementation: the networking with Retrofit, simplify threading of operations to be run on the Android main thread vs background thread
- RxBinding was used to simplify view bindings with EditTexts and Buttons.
- Persistence is done using Shared Preferences.  Simplest to get setup and string serialization/deserialization to JSON <-> POJO is straight forward with the help of GSON parser.
- An indeterminate progress bar will show up if rates are being fetched from the network.  I could add a "Last Updated" 

- Clean and contemporary UI presentation with the use of Material Design Android style Cards.
- More design could be done to handle numbers which exceed the card width.  But instead of spending more hours perfecting the UI, I chose to do more testing.
- Used a convenient load in animator library with the Recycler View just to make it a bit more UI pleasing.


## Goal:

#### Develop a currency conversion app that allows a user to convert an input value by any of the supplied rates.

- [ ] Fork this repo. Keep it public until we have been able to review it.
- [ ] Android: _Java/Kotlin_ | iOS: _Swift_
- [ ] exchange rates must be fetched from: http://fixer.io/  
- [ ] rates should be persisted locally and refreshed no more frequently than every 30 minutes
- [ ] user must be able to select the input currency from the list of supplied values

### Evaluation:
- [ ] App operates as asked
- [ ] No crashes or bugs
- [ ] SOLID principles
- [ ] Code is understandable and maintainable

UI Suggestion: Input field with a drop-down currency selector, and a list/grid of converted values below.

![UI Suggested Wireframe](ui_suggestion.png)
