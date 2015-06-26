(ns ccw-plugin-manager
  (:require [clojure.java.io       :as io]  
            [clojure.string        :as s]
            [ccw.e4.dsl            :refer :all]
            [ccw.e4.model          :as m]
            [ccw.eclipse           :as e]
            [ccw.file              :as f]
            [ccw.core.user-plugins :as p]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Alt+U S - re[S]tart user plugins
;; 

(defn restart []
  (let [r (p/start-user-plugins)]
    (try
      (let [skipped-plugins (deref r)]
        (e/info-dialog "User plugins"
          (str "User plugins have been restarted successfully!"
            (when (seq skipped-plugins)
              (str "\n\n"
                (s/join "\n\n" (map p/skipped-plugin-message skipped-plugins)))))))
      (catch Exception e
        (e/error-dialog "User plugins" (str "An error occured while starting User plugins: \n"
            (.getMessage e)))))))

(defcommand start-user-plugins "Start/restart user plugins" "Alt+U S"
  []
  ; call restart in a future so we do not block the UI thread
  (future (restart)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Alt+U N - create [N]ew user plugin
;; 

(defn validate-project-name
  ([s] (validate-project-name s (io/file (p/plugins-root-dir) s)))
  ([s proj-location]
    (or 
      (and (zero? (count s)) "You must type a project name")
      (e/validate-name-as-resource-type s :project)
      (and (e/project-exists s) (format "A project with name '%s' already exists" s))
      (e/validate-project-location proj-location))))

(defn create-user-plugin []
  (let [project-name @(e/input-dialog 
                        "New User Plugin" 
                        "Enter the name for your Plugin:" 
                        "plugin-name" 
                        validate-project-name)
        proj-location (io/file (p/plugins-root-dir) project-name)]
    
    (when-not (.exists proj-location)
      (.mkdirs proj-location)
      (spit (io/file proj-location "script.clj")
            (format "(ns %s)\n\n" project-name)))
    
    (when-not (e/project-exists project-name)
      (e/project-create project-name proj-location))
    
    (e/project-open project-name)
    
    (ccw.repl.Actions/connectToEclipseNREPL)
    
    (doseq [f (filter #(and (e/worskpace-file? %)
                            (= "clj" (e/file-extension %)))
                      (e/project-members project-name))]
      (-> f e/open-workspace-file (e/goto-editor-line -1)))))

(defcommand create-user-plugin-cmd "New User plugin" "Alt+U N"
  [] (create-user-plugin))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Alt+U I - Import all user plugins as projects in workspace
;; 

(defn create-update-projects []
  (doseq [p (p/user-plugins (p/plugins-root-dir))]
    (if-let [msg (validate-project-name (.getName p) p)]
      (e/error-dialog 
        "User Plugin"
        (format "Wont create project '%s' in workspace ; reason='%s'"
                p msg))
      (do
        (e/project-create (.getName p) p)
        (e/project-open (.getName p))
        (ccw.repl.Actions/connectToEclipseNREPL)))))

(defcommand create-user-plugins-projects
  "Import user plugins as projects in Workspace" "Alt+U I"
  [] (create-update-projects))
