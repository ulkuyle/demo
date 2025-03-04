INSERT INTO public.customers (customer_id, name, password, role)
VALUES (10, 'admin1', '$2a$12$D3vdX3Taq/ZqcAOoadtvOekvCV49vaMx9SxhyIs4YriP7YGVDF8wi', 'ROLE_ADMIN');

INSERT INTO public.customers (customer_id, name, password, role)
VALUES (11, 'user1', '$2a$12$D3vdX3Taq/ZqcAOoadtvOekvCV49vaMx9SxhyIs4YriP7YGVDF8wi', 'ROLE_USER');

INSERT INTO public.customers (customer_id, name, password, role)
VALUES (12, 'admin2', '$2a$12$D3vdX3Taq/ZqcAOoadtvOekvCV49vaMx9SxhyIs4YriP7YGVDF8wi', 'ROLE_ADMIN');

INSERT INTO assets (customer_id, asset_name, size, usable_size) VALUES
                                                                    (10, 'BTC', 5, 5),
                                                                    (11, 'ETH', 10, 10),
                                                                    (12, 'ABC', 10, 10),
                                                                    (10, 'TRY', 2000, 2000),
                                                                    (11, 'TRY', 3000, 3000),
                                                                    (12, 'TRY', 3000, 3000);
