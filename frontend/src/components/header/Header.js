import React, {useEffect, useState} from 'react';
import './Header.css';
import {Link, useNavigate} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import Cookie from "js-cookie";
import menu from '../../images/menu.png'

const Header = () => {

    const isAuth = useSelector(user => user.isAuth)
    const dispatch = useDispatch()
    const navigate = useNavigate()
    const [isMenuActive, setIsMenuActive] = useState(false);


    useEffect(() => {
        const main = document.querySelector('.main');
        const footer = document.querySelector('.footer');
        const menu = document.querySelector('.menu')
        if(isMenuActive) {
            if (main) {
                main.classList.add('none');
            }
            footer.classList.add('none')
            menu.classList.add('active-menu')
        }
        else {
            if (main) {
                main.classList.remove('none');
            }
            footer.classList.remove('none')
            menu.classList.remove('active-menu')
        }
    }, [isMenuActive]);

    const handleLoginClick = () => {
        const element = document.querySelector('.form-container');
        element.classList.add('active');
    }

    const handleLogoutClick = () => {
        Cookie.remove('jwt')
        dispatch({type: "SET_IS_AUTH", isAuth: false})
        navigate("/")
        setIsMenuActive(false)
    }

    const handleBurgerClick = () => {
        setIsMenuActive(!isMenuActive);
    }

    const handleAccountClick = () => {
        navigate("/account")
        setIsMenuActive(false);
    }

    return (
        <>
            <div className="header">
                <div className="logo"><Link to="/" onClick={() => setIsMenuActive(false)}>MarthaJoin</Link></div>
                <div className="nav">
                    <div className="links">
                        <div className="nav-link"><Link to="/">Как все работает</Link></div>
                        <div className="nav-link"><Link to="/">FAQs</Link></div>
                        <div className="nav-link"><Link to="/">Правила сервиса</Link></div>
                        <div className="nav-link"><Link to="/">Контакты</Link></div>
                    </div>
                    {isAuth ?
                        <div className="auth">
                            <Link to="/account">Личный кабинет</Link>
                            <div className="logout" onClick={handleLogoutClick}>Выйти</div>
                        </div>
                        :

                        <div className="login" onClick={handleLoginClick}>
                            Войти
                        </div>
                    }
                </div>
                <div className="burger" onClick={handleBurgerClick}>
                    <img src={menu} alt=""/>
                </div>
            </div>
            <div className="menu">
                {isAuth ?
                    <div className="menu-auth">
                        <div onClick={handleAccountClick}>Личный кабинет</div>
                        <div className="menu-logout" onClick={handleLogoutClick}>Выйти</div>
                    </div>
                    :

                    <div className="login" onClick={handleLoginClick}>
                        Войти
                    </div>
                }

                <div className="menu-links">
                    <div className="menu-nav-link"><Link to="/">Как все работает</Link></div>
                    <div className="menu-nav-link"><Link to="/">FAQs</Link></div>
                    <div className="menu-nav-link"><Link to="/">Правила сервиса</Link></div>
                    <div className="menu-nav-link"><Link to="/">Контакты</Link></div>
                </div>

            </div>
        </>
    );
};

export default Header;