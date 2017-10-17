(ns clojurecademy.core
  (:require [clojurecademy.controller.user :refer [update-account-details change-password resend-activation]]
            [clojurecademy.controller.auth :refer [signup login logout send-reset-pass-email reset-password]]
            [clojurecademy.controller.course :refer [release]]
            [clojurecademy.util :as util]
            [goog.dom :as dom]))

(enable-console-print!)


;;Home - Signup
(util/set-event-handler! "onclick" "signup-button" signup)
(util/set-event-handler! "onkeydown" "signup-email" (util/call-fn-if-enter-pressed signup))
(util/set-event-handler! "onkeydown" "signup-username" (util/call-fn-if-enter-pressed signup))
(util/set-event-handler! "onkeydown" "signup-password" (util/call-fn-if-enter-pressed signup))


;;Login
(util/set-event-handler! "onclick" "login-button" login)
(util/set-event-handler! "onkeydown" "login-username-or-email" (util/call-fn-if-enter-pressed login))
(util/set-event-handler! "onkeydown" "login-password" (util/call-fn-if-enter-pressed login))


;;Account
(util/set-event-handler! "onclick" "update-account-details-button" update-account-details)
(util/set-event-handler! "onclick" "change-password-button" change-password)
(util/set-event-handler! "onclick" "logout" logout)
(util/set-event-handler! "onclick" "resend-mail-link" resend-activation)


;;Release
(util/set-event-handler! "onclick" "release-button" release)


;;Forgot password
(util/set-event-handler! "onclick" "send-recovery-email-button" send-reset-pass-email)
(util/set-event-handler! "onkeydown" "forgot-pass-email" (util/call-fn-if-enter-pressed send-reset-pass-email))

;;Reset Password
(util/set-event-handler! "onclick" "reset-password-button" reset-password)
(util/set-event-handler! "onkeydown" "reset-password" (util/call-fn-if-enter-pressed reset-password))