#!/bin/bash
java -cp "control/build/libs/control.jar:build/libs/business-logic.jar:build/libs/data-access.jar:build/libs/di.jar:build/libs/jdbc.jar:build/libs/payment.jar:build/libs/security.jar:build/libs/postgresql-42.7.5.jar" traintickets.control.Main
