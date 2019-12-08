%% Implemented by Martin Helsø (martibhe@math.uio.no)
%% Version: 23-09-2019


\NeedsTeXFormat{LaTeX2e}


\ProvidesClass{uioposter}


\DeclareOption{english}{\def\uioposter@lang{english}}
\DeclareOption{norsk}
{
    \def\uioposter@lang{norsk}
    \pretolerance = 2000
    \tolerance    = 6000
    \hbadness     = 6000
}


\DeclareOption{portrait}
{
    \PassOptionsToPackage{orientation = portrait}{beamerposter}
    \def\uioposter@orientation{}
}
\DeclareOption{landscape}
{
    \PassOptionsToPackage{orientation = landscape}{beamerposter}
    \def\uioposter@orientation{landscape}
}


\DeclareOption{a0paper}{\def\uioposter@size{a0paper}}
\DeclareOption{a1paper}{\def\uioposter@size{a1paper}}
\DeclareOption{a2paper}{\def\uioposter@size{a2paper}}
\DeclareOption{a3paper}{\def\uioposter@size{a3paper}}
\DeclareOption{a4paper}{\def\uioposter@size{a4paper}}
\DeclareOption{a5paper}{\def\uioposter@size{a5paper}}
\DeclareOption{a6paper}{\def\uioposter@size{a6paper}}
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{beamer}}


\ExecuteOptions{a0paper}
\ExecuteOptions{english}
\ExecuteOptions{portrait}
\ProcessOptions
\relax


\LoadClass[t]{beamer}


\RequirePackage[size = a0, scale = 1.5]{beamerposter}
\RequirePackage{etoolbox}
\RequirePackage{pgfpages}
\RequirePackage[utf8]{inputenx}
\RequirePackage[T1]{fontenc}
\RequirePackage{calc}
\RequirePackage[final]{microtype}
\RequirePackage{lmodern}
\RequirePackage[scaled]{beramono}


\hypersetup{colorlinks,
            urlcolor  = uiolink,
            linkcolor =,
            citecolor =,
            filecolor =}
\renewcommand{\sfdefault}{phv}
\DeclareFontShape{OMX}{cmex}{m}{n}
{
    <-7.5>    cmex7
    <7.5-8.5> cmex8
    <8.5-9.5> cmex9
    <9.5->    cmex10
}{}
\SetSymbolFont{largesymbols}{normal}{OMX}{cmex}{m}{n}
\SetSymbolFont{largesymbols}{bold}  {OMX}{cmex}{m}{n}
\setlength{\marginparwidth}{2cm}
\setlength{\emergencystretch}{1em}
\frenchspacing

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\usefonttheme{serif}
\useinnertheme{rectangles}
\setbeamertemplate{navigation symbols}{}
\setbeamertemplate{caption}[numbered]
\setbeamertemplate{caption label separator}{: }
\setbeamertemplate{qed symbol}{\(\blacksquare\)}
\setbeamertemplate{itemize items}{\color{itemize item.fg}\(\blacksquare\)}


\setlength{\labelsep}{3ex}
\setlength{\leftmargini}{18ex}
\setbeamersize{text margin left = 3cm, text margin right = 3cm}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\definecolor{matiesRed}{RGB}{96, 34, 59}
\definecolor{matiesYellow}{RGB}{255, 155, 51}
\definecolor{matiesGray}{RGB}{140, 151, 154}
\definecolor{matiesBlack}{RGB}{0, 0, 0}
\definecolor{matiesScience}{RGB}{180, 22, 44}
\definecolor{uiowhite}{cmyk}{0, 0, 0, 0}
\definecolor{matiesBlack}{cmyk}{0, 0, 0, 1}
\definecolor{uioheader}{cmyk}{0.578, 0.894, 0, 0.552}
\definecolor{uiobackground}{cmyk}{0, 0, 0, 0.2}
\definecolor{uiolink}{HTML}{0B5A9D}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\setbeamercolor{structure}{fg = uioheader}
\setbeamercolor{alerted text}{fg = matiesYellow}
\setbeamercolor{background canvas}{fg = matiesBlack, bg = uiobackground}
\setbeamercolor{headline}{fg = uiowhite, bg = matiesRed}
\setbeamercolor{title in headline}{fg = uiowhite}
\setbeamercolor{author in headline}{fg = uiowhite}
\setbeamercolor{footline}{fg = uiowhite, bg = matiesRed}


\setbeamercolor{block title}{fg = uiowhite, bg = matiesRed}
\setbeamercolor{block title example}{fg = uiowhite, bg = matiesBlack}
\setbeamercolor{block title alerted}{fg = uiowhite, bg = matiesYellow}


\setbeamercolor{block body} {fg = matiesBlack, bg = uiowhite}
\setbeamercolor{block body example} {fg = matiesBlack, bg = uiowhite}
\setbeamercolor{block body alerted} {fg = matiesBlack, bg = uiowhite}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\setbeamerfont{title in headline}{family = \sffamily,
                                  series = \bfseries,
                                  size   = \huge}
\setbeamerfont{author in headline}{family = \sffamily,
                                   series = \bfseries,
                                   size   = \Large}
\setbeamerfont{institute}{family = \sffamily,
                          series = \mdseries,
                          size   = \normalsize}


\setbeamerfont{block title}{family = \sffamily,
                            series = \bfseries,
                            size   = \large}
\setbeamerfont{block title alerted}{family = \sffamily,
                                    series = \bfseries,
                                    size   = \large}
\setbeamerfont{block title example}{family = \sffamily,
                                    series = \bfseries,
                                    size   = \large}


\setbeamerfont{block body}{family = \rmfamily,
                           series = \normalfont,
                           size   = \small}
\setbeamerfont{block body example}{family = \rmfamily,
                                   series = \normalfont,
                                   size   = \normalsize}
\setbeamerfont{block body alerted}{family = \rmfamily,
                                   series = \normalfont,
                                   size   = \normalsize}


\setbeamerfont{item projected}{family = \sffamily,
                               series = \bfseries,
                               size   = \small}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\setbeamertemplate{headline}
{
    \begin{beamercolorbox}[wd = \paperwidth,
                           leftskip  = 3cm,
                           rightskip = 3cm plus 20cm]{headline}
        % \vskip+0.5cm
        % \begin{columns}
        % \begin{column}{width=0.8\textwidth}
        %     \usebeamerfont{title in headline}
        %     \usebeamercolor{title in headline}
        % \end{column}
        % \begin{column}
        %     \begin{center}
        %      \includegraphics[width=0.2\textwidth]{matiesBrandImages/SU100_ID_StackEndorsement.pdf}      
        %      \end{center}
        % \end{column}
        % \end{columns}
        
        \usebeamerfont{title in headline}
        \usebeamercolor{title in headline}
        \color{fg}
        % \vskip0ex
         \vspace{1.5cm}
        % \hskip-0.01ex

        \begin{columns}[totalwidth=\textwidth]
            \begin{column}{0.04\textwidth}
            \end{column}
            \begin{column}{0.76\textwidth}
                \inserttitle
                 \vskip1ex
                \usebeamerfont{author in headline}
                \usebeamercolor{author in headline}
                \color{fg}
                \hskip-0.05ex
                \insertauthor
                \ifdefempty{\insertinstitute}{}
                {
                    \vskip1ex
                    \usebeamerfont{institute}
                    \def\beamer@andinst{\quad}
                    \insertinstitute
                }
            \end{column}
            \begin{column}{0.2\textwidth}
                \begin{center}
                \vspace{8cm}
                \raisebox{0mm}[0pt][0pt]{%
                \makebox[\textwidth][c]{\includegraphics[scale=1.2]{matiesBrandImages/SU100_CustomWhiteVert.png}}
                }
                 \end{center}
            \end{column}
        \end{columns}
        
       
        \vskip3cm
    \end{beamercolorbox}
    \vskip1cm
}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\setbeamertemplate{footline}
{
    \leavevmode
    \begin{beamercolorbox}[wd = 0.8\paperwidth]{footline}
        \vskip1.77cm
        \hskip4cm
        \includegraphics[height=6.2cm]{matiesBrandImages/SU100_ID_SubGraphic.pdf}
        \vskip1.77cm
    \end{beamercolorbox}
    \begin{beamercolorbox}[wd = 0.2\paperwidth]{footline}
        \vspace{0.56cm}
        \begin{figure}
          \centering
          \includegraphics[height=7cm]{img/providentia.png}
          \caption*{\sffamily \tiny Source Code}
        \end{figure}
        \vspace{-0.09cm}
    \end{beamercolorbox}
}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\setbeamertemplate{block begin}
{
    \setbeamercolor{itemize item}  {fg = block title.bg}
    \setbeamercolor{item projected}{fg = block title.fg,
                                    bg = block title.bg}
    \setbeamercolor{description item}{fg = structure}

    \begin{beamercolorbox}[ht        = 3.2 ex,
                           dp        = 1.3 ex,
                           leftskip  = 2 ex,
                           rightskip = 2 ex]
                          {block title}

        \usebeamerfont*{block title}
        \insertblocktitle
        
    \end{beamercolorbox}

    \nointerlineskip

    \begin{beamercolorbox}[dp        = 2 ex,
                           leftskip  = 2 ex,
                           rightskip = 2 ex,
                           vmode]
                          {block body}

        \usebeamerfont{block body}
        \vskip2ex
        \begin{minipage}{\textwidth-4ex}
             \setlength{\parskip}{1.5ex}
             \setlength{\abovecaptionskip}{1ex}
}


\setbeamertemplate{block end}
{
        \end{minipage}
    \vskip0.2ex
    \end{beamercolorbox}
    \vskip3cm
}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\setbeamertemplate{block example begin}
{
    \setbeamercolor{itemize item}  {fg = block title example.bg}
    \setbeamercolor{item projected}{fg = block title example.fg,
                                    bg = block title example.bg}
    \setbeamercolor{description item}{fg = structure}

    \begin{beamercolorbox}[ht        = 3.2 ex,
                           dp        = 1.3 ex,
                           leftskip  = 2 ex,
                           rightskip = 2 ex]
                          {block title example}

        \usebeamerfont*{block title example}
        \insertblocktitle

    \end{beamercolorbox}

    \nointerlineskip

    \begin{beamercolorbox}[dp        = 2 ex,
                           leftskip  = 2 ex,
                           rightskip = 2 ex,
                           vmode]
                          {block body example}

        \usebeamerfont{block example body}
        \vskip2ex
        \begin{minipage}{\textwidth-4ex}
             \setlength{\parskip}{1.5ex}
             \setlength{\abovecaptionskip}{1ex}
}


\setbeamertemplate{block example end}
{
        \end{minipage}
    \vskip0.2ex
    \end{beamercolorbox}
    \vskip3cm
}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\setbeamertemplate{block alerted begin}
{
    \setbeamercolor{itemize item}  {fg = block title alerted.bg}
    \setbeamercolor{item projected}{fg = block title alerted.fg,
                                    bg = block title alerted.bg}
    \setbeamercolor{description item}{fg = structure}

    \begin{beamercolorbox}[ht        = 3.2 ex,
                           dp        = 1.3 ex,
                           leftskip  = 2 ex,
                           rightskip = 2 ex]
                          {block title alerted}

        \usebeamerfont*{block title alerted}
        \insertblocktitle

    \end{beamercolorbox}

    \nointerlineskip

    \begin{beamercolorbox}[dp        = 2 ex,
                           leftskip  = 2 ex,
                           rightskip = 2 ex,
                           vmode]
                          {block body alerted}

        \usebeamerfont{block body alerted}
        \vskip2ex
        \begin{minipage}{\textwidth-4ex}
             \setlength{\parskip}{1.5ex}
             \setlength{\abovecaptionskip}{1ex}
}


\setbeamertemplate{block alerted end}
{
        \end{minipage}
    \vskip0.2ex
    \end{beamercolorbox}
    \vskip3cm
}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\newcommand{\enumitem}[1]
{
    \leavevmode\unskip
    \hbox
    {%
        \usebeamerfont*{item projected}%
        \usebeamercolor[bg]{item projected}%
        \vrule width2.25ex height1.85ex depth.4ex%
        \hskip-2.25ex%
        \hbox to2.25ex
        {%
            \hfil%
            \color{fg}#1%
            \hfil
        }%
    }%
}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\pgfpagesdeclarelayout{resize and center}
{
    \def\pgfpageoptionborder{0pt}
}
{
    \pgfpagesphysicalpageoptions
    {
        logical pages   = 1,
        physical height = \pgfpageoptionheight,
        physical width  = \pgfpageoptionwidth
    }
    \pgfpageslogicalpageoptions{1}
    {
        resized width  = \pgfphysicalwidth,
        resized height = \pgfphysicalheight,
        border shrink  = \pgfpageoptionborder,
        center         = \pgfpoint{.5\pgfphysicalwidth}{.5\pgfphysicalheight}
    }
}


\ifdefstring{\uioposter@size}{a0paper}{}
{
    \pgfpagesuselayout{resize and center}[\uioposter@size,
                                          \uioposter@orientation]
}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

\endinput