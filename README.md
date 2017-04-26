#Mobile App Developer Coding Challenge

## Developer Notes:

### Assumptions/Constraints:

- Input left vs right of the decimal is separated into two fields.  This is just to keep the input error checking as simple as possible.  If I wanted to have it one big input text field, more error checking can be done in an "Error Check" layer before data is passed to the Presenter.
- In the "dollar" field, any non-digit will pass the focus onto the cents view.  This is a UX convenience feature and is not explicitly stated on the UI.
- Max input "dollar" value (the number to the left of the decimal) is 2147483647 (+ve 32-bit integer).  Certainly this can be increased if we used a different java.lang.Number like Long. 
- Max number of digits in "cents" capped to 2.  This can also be increased and more error checking would be required.
- Rates are only fetched when the CONVERT button is pressed.  If rates should be fetched as the user types, debouncing should be added.


### Design Decisions:

- The MVP paradigm was used to isolate components with the app to give clarity of responsibility within each layer. 
- Retrofit was used since we are dealing with a REST API Web Service.  It makes for parsing JSON data (via GSON) much easier.
- RxAndroid was used to reduce complexities with the architecture and implementation: the networking with Retrofit, simplify threading of operations to be run on the Android main thread vs background thread, and reduce potential of nested networking callbacks (which AsyncTask/Volley could present)
- RxBinding was used to simplify view bindings with EditTexts and Buttons.
- Persistence is done using Shared Preferences.  Simplest to get setup and string serialization/deserialization to JSON <-> POJO was most simple with GSON.

- Clean and contemporary UI presentation with the use of Material Design Android style Cards.
- More design could be done to handle long numbers which exceed the card width.  But instead of spending more hours perfecting the UI, I chose to do more testing.


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
