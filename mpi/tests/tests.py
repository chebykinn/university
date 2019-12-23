import uuid

from time import sleep
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By


def do_login(selenium, user):
    login_form = WebDriverWait(selenium, 5).until(EC.element_to_be_clickable((By.ID, 'login')))
    password_form = selenium.find_element_by_id('password')
    submit_butt = selenium.find_element_by_xpath("//input[@type='submit']")

    login_form.send_keys('{}@t.t'.format(user))
    password_form.send_keys('qwerty')
    submit_butt.click()


def test_login(selenium):
    selenium.get('https://ghostflow.tk')

    do_login(selenium, 'customer')


def test_logout(selenium):
    selenium.get('https://ghostflow.tk')

    do_login(selenium, 'customer')

    logout_link = WebDriverWait(selenium, 5).until(EC.element_to_be_clickable((By.ID, 'linkLogout')))
    logout_link.click()

    assert "GhostFlow - Выход из системы" in selenium.title


def test_register(selenium):
    selenium.get('https://ghostflow.tk')

    register_link = WebDriverWait(selenium, 5).until(EC.element_to_be_clickable((By.ID, 'linkRegister')))
    register_link.click()

    email_form = selenium.find_element_by_id('email')
    name_form = selenium.find_element_by_id('name')
    password_form = selenium.find_element_by_id('password')
    password_repeat_form = selenium.find_element_by_id('password_repeat')

    user_id = uuid.uuid4();
    email_form.send_keys('test-{}@t.t'.format(user_id))
    name_form.send_keys('test-{}'.format(user_id))
    password_form.send_keys('qwerty')
    password_repeat_form.send_keys('qwerty')

    submit_butt = selenium.find_element_by_xpath("//input[@type='submit']")
    submit_butt.click()


def test_delete_account(selenium):
    pass


def test_leave_review(selenium):
    selenium.get('https://ghostflow.tk')

    do_login(selenium, 'customer')

    fb_link = WebDriverWait(selenium, 5).until(EC.element_to_be_clickable((By.ID, 'linkFeedbacks')))
    fb_link.click()

    create_butt = WebDriverWait(selenium, 5).until(EC.presence_of_element_located((By.XPATH, '//button')))
    create_butt.click()

    text_form = selenium.find_element_by_id('feedback_text')
    text_form.send_keys('test review')

    rating = Select(selenium.find_element_by_id('feedback_rating'))
    rating.select_by_value('4')

    submit_butt = selenium.find_element_by_xpath("//input[@type='submit']")
    submit_butt.click()

    # TODO check list


def test_view_review(selenium):
    pass


def test_create_customer_ticket(selenium):
    selenium.get('https://ghostflow.tk')
    do_login(selenium, 'customer')

    ct_link = WebDriverWait(selenium, 5).until(EC.element_to_be_clickable((By.ID, 'linkCreateTicket')))
    ct_link.click()

    uniq = uuid.uuid4();
    title_text = 'test ghost ticket title' + str(uniq)
    body_text = 'test ghost ticket body' + str(uniq)
    title_form = selenium.find_element_by_id('title')
    text_form = selenium.find_element_by_id('body')

    title_form.send_keys(title_text)
    text_form.send_keys(body_text)

    submit_butt = selenium.find_element_by_xpath("//input[@type='submit']")
    submit_butt.click()

    WebDriverWait(selenium, 5).until(EC.alert_is_present(), 'Заявка успешно создана')

    alert = selenium.switch_to.alert
    alert.accept()

    row_path = "//td[contains(text(),'{}')]".format(title_text)
    row = WebDriverWait(selenium, 5).until(EC.presence_of_element_located((By.XPATH, row_path)))
    row.click()

    assert 'Заявка' in selenium.title

    body_row_path = "//p[contains(text(),'{}')]".format(body_text)
    body_row = WebDriverWait(selenium, 5).until(EC.presence_of_element_located((By.XPATH, body_row_path)))


def test_delete_customer_ticket(selenium):
    pass


def test_create_repair_ticket(selenium):
    selenium.get('https://ghostflow.tk')
    do_login(selenium, 'leadoperative')

    ct_link = WebDriverWait(selenium, 5).until(EC.element_to_be_clickable((By.ID, 'linkInternals')))
    ct_link.click()

    sleep(1)

    butt_path = "//button[contains(text(),'Создать')]"
    create_butt = WebDriverWait(selenium, 5).until(EC.presence_of_element_located((By.XPATH, butt_path)))
    create_butt.click()


    uniq = uuid.uuid4();
    title_text = 'test repair ticket title' + str(uniq)
    body_text = 'test repair ticket body' + str(uniq)
    title_form = selenium.find_element_by_id('title')
    text_form = selenium.find_element_by_id('body')

    title_form.send_keys(title_text)
    text_form.send_keys(body_text)

    submit_butt = selenium.find_element_by_xpath("//input[@type='submit']")
    submit_butt.click()

    WebDriverWait(selenium, 5).until(EC.alert_is_present(), 'Готово. Вы - превосходны!')

    alert = selenium.switch_to.alert
    alert.accept()

    row_path = "//td[contains(text(),'{}')]".format(title_text)
    row = WebDriverWait(selenium, 5).until(EC.presence_of_element_located((By.XPATH, row_path)))
    row.click()

    assert 'Заявка на ремонт' in selenium.title

    body_row_path = "//p[contains(text(),'{}')]".format(body_text)
    body_row = WebDriverWait(selenium, 5).until(EC.presence_of_element_located((By.XPATH, body_row_path)))


def test_delete_repair_ticket(selenium):
    pass


def test_notify_repair_ticket(selenium):
    pass


def test_change_repair_ticket_status(selenium):
    selenium.get('https://ghostflow.tk')
    do_login(selenium, 'rnd')

    ct_link = WebDriverWait(selenium, 5).until(EC.element_to_be_clickable((By.ID, 'linkInternals')))
    ct_link.click()

    last_t_path = "//a[@style='text-decoration: none; color: black;']"
    first = WebDriverWait(selenium, 5).until(EC.presence_of_element_located((By.XPATH, last_t_path)))
    ticket_id = first.text
    first.click()

    assert 'Заявка на ремонт' in selenium.title
    id_path = "//td[contains(text(),'{}')]".format(ticket_id)
    print(id_path)
    id_elem = WebDriverWait(selenium, 5).until(EC.presence_of_element_located((By.XPATH, id_path)))

    butt_path = "//button"
    accept_butt = WebDriverWait(selenium, 5).until(EC.presence_of_element_located((By.XPATH, butt_path)))
    accept_butt.click()

    WebDriverWait(selenium, 5).until(EC.alert_is_present())
    alert = selenium.switch_to.alert
    alert.accept()

    WebDriverWait(selenium, 5).until(EC.alert_is_present())
    alert = selenium.switch_to.alert
    alert.accept()
    pass


def test_view_accepted_task(selenium):
    pass


def test_accept_task(selenium):
    pass


def test_move_task_to_operatives(selenium):
    pass


def test_close_task_reason(selenium):
    pass
