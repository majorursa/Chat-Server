;; set the port of the Chat Server
(defvar port 1337)

;; set the host of the Chat Server
(defvar host "localhost")

(defun  my-chat () 
  "Chat client for chat server assignment"
  (interactive)
  ;; open-network-stream creates a socket to the Chat Server
  ;; setq associates the socket with the pointer "chat-client"
  (setq chat-client (open-network-stream "chat-room" "chat-room" host port)) 
  (while (not (string= (setq answer (read-from-minibuffer "> ")) "quit"))
    ;; process-send-string writes the string answer to the socket assoc with chat-client
    (process-send-string chat-client (format "%s" answer))
    (switch-to-buffer "chat-room")))
