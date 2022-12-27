INSERT INTO `users` (`id`, `created_date`, `email`, `name`, `pwd`, `rol`, `updated_date`, `valid_until`)
 SELECT 'admin', '2022-12-01 00:00:00.000000', 'carlos.sivori@gmail.com',
        'Administrador del Sistema', 'benjo', 'ADMINISTRADOR', '2022-12-01', '2122-12-23'
  WHERE NOT EXISTS (SELECT 1 FROM `users` WHERE `id` = 'admin');