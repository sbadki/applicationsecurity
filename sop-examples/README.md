## sop-example  ##

This project is based on Single Origin Policy (SOP). This consist of three sites, running on Apache2 server. 
One pointing to parent domain and other two pointing to sub-domains. This project explains how sites interact with each 
other with different examples.


1. Parent site refers to http://example.com/example/alicebob.html
2. Child site - http://alice.example.com/alice/alice.html
3. Child site - http://bob.example.com/bob/bob.html


Refer: https://sbadki.github.io/applicationsecurity/posts/2023-03-10-soap.html this block for more details.


## How to configure on Kali linux ##

1. Checkout all the three folders at location:/var/www/html/
2. Provide configuration for all three sites at : /etc/apache2/sites-available/. Example for alice-example.com is shown below.
   
alice.conf:

       ServerAlias www.alice.example.com
       ServerName alice.example.com
       ServerAdmin webmaster@localhost
       DocumentRoot /var/www/html/alice


3. Specify local site mapping with the host in /etc/hosts file
    
       127.0.0.1 alice.example.com
       127.0.0.1 bob.example.com
       127.0.0.1 example.com
