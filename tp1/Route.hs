
module Route ( Route, newR, inOrderR, inRouteR)
  where
    
import Data.List (elemIndex)

data Route = Rou [ String ] deriving (Eq, Show)

newR :: [ String ] -> Route                    -- construye una ruta segun una lista de ciudades
newR [] = Rou []
newR ciudades = Rou ciudades

inOrderR :: Route -> String -> String -> Bool  -- indica si la primer ciudad consultada esta antes que la segunda ciudad en la ruta
inOrderR (Rou ciudades) ciudad1 ciudad2 | index1 == Nothing || index2 == Nothing = False
                                        | index1 <=  index2 = True
                                        | otherwise = False
                                        where
                                          index1 = elemIndex ciudad1 ciudades
                                          index2 = elemIndex ciudad2 ciudades

inRouteR :: Route -> String -> Bool -- indica si la ciudad consultada está en la ruta
inRouteR (Rou ciudades) str = elem str ciudades