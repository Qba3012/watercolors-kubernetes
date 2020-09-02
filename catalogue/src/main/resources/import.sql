INSERT INTO painting (id,category,availability,width,height,main_image,create_date_time,description,price,title) VALUES (1,'birds',1,200,300,'http://localhost:8080/images/1',CURRENT_TIMESTAMP,'TEST DESCIRPTION',25.6,'TEST TITLE');

INSERT INTO  image_url(id,painting_id,url) VALUES (1,1,'http://localhost:8080/images/1');
INSERT INTO  image_url(id,painting_id,url) VALUES (2,1,'http://localhost:8080/images/2');
INSERT INTO  image_url(id,painting_id,url) VALUES (3,1,'http://localhost:8080/images/3');