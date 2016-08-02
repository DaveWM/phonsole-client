(ns phonsole-client.pages.help)

(defn code [code]
  [:span {:class "code"} code])

(defn help-page []
  [:div {:class "help-page"}
   [:h2 "Help"]
   [:p "Welcome to phonsole, a way to view console output from your PC on your phone or tablet."]
   [:h4 "Getting Started"]
   [:ol
    [:li "Make sure you have nodejs and npm installed on your PC"]
    [:li "Run " (code "npm install -g phonsole")]
    [:li "Pipe the command you want to run into phonsole, e.g. " (code "echo hello | phonsole") ", and log in using the same account you used to log in here"]
    [:li "You should now see the console output on the " [:a {:href "/"} "main page"]]]
   [:p "Phonsole is completely open source. It's split into 3 parts: "
    [:a {:href "https://github.com/DaveWM/phonsole-client"} "client"] ", "
    [:a {:href "https://github.com/DaveWM/phonsole-server"} "server"] " and "
    [:a {:href "https://github.com/DaveWM/phonsole-cli"} "CLI"] ". "
    "All feature requests, bug reports and PRs are welcome."]])
