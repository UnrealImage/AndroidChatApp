# !/usr/bin/env python
# -*- coding: utf-8 -*-
import json
import tornado.web
import requests
import json
from tornado.httputil import url_concat
from settings import settings

import coloredlogs
coloredlogs.install(level='DEBUG')

import mysqlDb.mysqlDb_helper as database
import json


# noinspection PyAbstractClass
class BaseHandler(tornado.web.RequestHandler):
    """
    A class to collect common handler methods - all other handlers should
    subclass this one.
    """

    def check_user(self):
        try:
            user_id = self.get_argument('user_id')
            password = self.get_argument('password')
            if database.checkuser(user_id, password):
                return True
            else:
                return False
        except Exception as error:
            print error
            self.res_status['msg'] = str(error)
            self.write(json.dumps(self.res_status))
            self.set_status(404)
            self.finish()
            return False