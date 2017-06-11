# !/usr/bin/env python
# -*- coding: utf-8 -*-

"""
@author: morc
@contact: 709403987ac@gmail.com
@create: 16/7/24
"""


from handlers.foo import FooHandler
from handlers.login import LoginHandler
from handlers.register import RegisterHandler
from handlers.heart import HeartHandler
from handlers.postmsg import PostmsgHandler
from handlers.change import ChangeHandler
from handlers.getcontext import GetContextHandler
from handlers.getmsg import GetmsgHandler

url_patterns = [

    #Test server
    (r"/login", LoginHandler),
    (r"/foo", FooHandler),
    (r"/register", RegisterHandler),
    (r"/heart", HeartHandler),
    (r"/postmsg", PostmsgHandler),
    (r"/change", ChangeHandler),
    (r"/getcontext", GetContextHandler),
    (r"/getmsg", GetmsgHandler),
]
