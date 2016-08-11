(ns phonsole-client.pages.help)

(defn code [code]
  [:span {:class "code"} code])

(defn help-page []
  [:div {:class "help-page"}
   [:h2 "Help"]
   [:p "Welcome to phonsole, a way to view console output from your PC on your phone or tablet."]
   [:h4 "Getting Started"]
   [:ol
    [:li "Make sure you have nodejs installed on your PC (see " [:a {:href "https://nodejs.org"} "here"] " for help)"]
    [:li "Run " (code "npm install -g phonsole")]
    [:li "Pipe the command you want to run into phonsole, e.g. " (code "echo hello | phonsole") ", and log in using the same account you used to log in here"]
    [:li "You should now see the console output on the " [:a {:href "/"} "main page"]]]
   [:h4 "Adding to your home screen"]
   [:p "Phonsole is a " [:a {:href "https://developers.google.com/web/progressive-web-apps/"} "progressive web app"] ", so you can add it to your home screen and use it like a normal app. When you visit this site 2 or 3 times, you should be shown a banner asking you to add the app to your home screen. If this doesn't appear, you can add it manually by following " [:a {:href "http://www.howtogeek.com/196087/how-to-add-websites-to-the-home-screen-on-any-smartphone-or-tablet/"} "this guide."]]
   [:h4 "Open Source"]
   [:p "Phonsole is completely open source, and hosted on GitHub. It's split into 3 parts: "
    [:a {:href "https://github.com/DaveWM/phonsole-client"} "client"] ", "
    [:a {:href "https://github.com/DaveWM/phonsole-server"} "server"] " and "
    [:a {:href "https://github.com/DaveWM/phonsole-cli"} "CLI"] ". "
    "All feature requests, bug reports and PRs are welcome."]])
